package com.example.blogs.app.logging;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClientBuilder;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.io.Serializable;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Production-grade CloudWatch Logs appender for Log4j2.
 * Features:
 * - Batching for efficiency
 * - Async processing with backpressure
 * - Automatic retry with exponential backoff
 * - Graceful shutdown with flush
 * - LocalStack support
 * - Proper error handling
 */
@Plugin(name = "CloudWatch", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class CloudWatchAppender extends AbstractAppender {

    private static final int MAX_BATCH_SIZE = 100;
    private static final int MAX_BATCH_BYTES = 1_048_576; // 1MB
    private static final int QUEUE_CAPACITY = 10000;
    private static final long FLUSH_INTERVAL_MS = 1000;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;

    private final CloudWatchLogsClient cloudWatchClient;
    private final String logGroupName;
    private final String logStreamName;
    private final BlockingQueue<InputLogEvent> eventQueue;
    private final ExecutorService writerExecutor;
    private final AtomicReference<String> sequenceToken;
    private final AtomicBoolean running;
    private final boolean createLogGroup;
    private final boolean createLogStream;

    /**
     * Constructs a CloudWatch appender with the specified configuration and resources.
     *
     * @param name             the appender name
     * @param filter           the filter to apply to log events
     * @param layout           the layout for formatting log messages
     * @param ignoreExceptions whether to ignore exceptions during logging
     * @param properties       additional properties for the appender
     * @param cloudWatchClient the AWS CloudWatch Logs client
     * @param logGroupName     the CloudWatch log group name
     * @param logStreamName    the CloudWatch log stream name
     * @param createLogGroup   whether to automatically create the log group if it doesn't exist
     * @param createLogStream  whether to automatically create the log stream if it doesn't exist
     */
    protected CloudWatchAppender(
            String name,
            Filter filter,
            Layout<? extends Serializable> layout,
            boolean ignoreExceptions,
            Property[] properties,
            CloudWatchLogsClient cloudWatchClient,
            String logGroupName,
            String logStreamName,
            boolean createLogGroup,
            boolean createLogStream) {

        super(name, filter, layout, ignoreExceptions, properties);

        this.cloudWatchClient = cloudWatchClient;
        this.logGroupName = logGroupName;
        this.logStreamName = logStreamName;
        this.createLogGroup = createLogGroup;
        this.createLogStream = createLogStream;
        this.eventQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        this.sequenceToken = new AtomicReference<>();
        this.running = new AtomicBoolean(true);

        // Initialize CloudWatch resources
        initializeCloudWatchResources();

        // Start background processing
        this.writerExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "CloudWatch-Writer-" + logStreamName);
            t.setDaemon(true);
            return t;
        });

        // Start event processor
        writerExecutor.submit(this::processEvents);
    }

    /**
     * Initializes CloudWatch log group and log stream if configured to create them.
     */
    private void initializeCloudWatchResources() {
        try {
            if (createLogGroup) {
                ensureLogGroupExists();
            }
            if (createLogStream) {
                ensureLogStreamExists();
            }
        } catch (Exception e) {
            error("Failed to initialize CloudWatch resources", e);
        }
    }

    /**
     * Creates the CloudWatch log group if it doesn't already exist.
     */
    private void ensureLogGroupExists() {
        try {
            cloudWatchClient.createLogGroup(CreateLogGroupRequest.builder()
                    .logGroupName(logGroupName)
                    .build());
            LOGGER.info("Created CloudWatch log group: {}", logGroupName);
        } catch (ResourceAlreadyExistsException e) {
            LOGGER.debug("CloudWatch log group already exists: {}", logGroupName);
        } catch (SdkException e) {
            LOGGER.error("Failed to create log group: {}", logGroupName, e);
        }
    }

    /**
     * Creates the CloudWatch log stream if it doesn't already exist.
     */
    private void ensureLogStreamExists() {
        try {
            cloudWatchClient.createLogStream(CreateLogStreamRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamName(logStreamName)
                    .build());
            LOGGER.info("Created CloudWatch log stream: {}/{}", logGroupName, logStreamName);
        } catch (ResourceAlreadyExistsException e) {
            LOGGER.debug("CloudWatch log stream already exists: {}/{}", logGroupName, logStreamName);
        } catch (SdkException e) {
            LOGGER.error("Failed to create log stream: {}/{}", logGroupName, logStreamName, e);
        }
    }

    /**
     * Appends a log event to the CloudWatch queue for asynchronous processing.
     *
     * @param event the log event to append
     */
    @Override
    public void append(LogEvent event) {
        if (!running.get()) {
            return;
        }

        try {
            Layout<? extends Serializable> layout = getLayout();
            String message;

            if (layout != null) {
                byte[] data = layout.toByteArray(event);
                message = new String(data, java.nio.charset.StandardCharsets.UTF_8);
            } else {
                message = event.getMessage().getFormattedMessage();
            }

            InputLogEvent logEvent = InputLogEvent.builder()
                    .timestamp(event.getTimeMillis())
                    .message(message)
                    .build();

            if (!eventQueue.offer(logEvent, 100, TimeUnit.MILLISECONDS)) {
                LOGGER.warn("CloudWatch event queue is full, dropping log event");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.warn("Interrupted while queuing log event", e);
        } catch (Exception e) {
            LOGGER.error("Error appending log event", e);
        }
    }

    /**
     * Processes queued log events in batches and sends them to CloudWatch.
     * Runs continuously in a background thread until shutdown.
     */
    private void processEvents() {
        List<InputLogEvent> batch = new ArrayList<>();
        long lastFlushTime = System.currentTimeMillis();

        while (running.get() || !eventQueue.isEmpty()) {
            try {
                // Wait for events with timeout
                InputLogEvent event = eventQueue.poll(500, TimeUnit.MILLISECONDS);

                if (event != null) {
                    batch.add(event);
                    drainQueueToBatch(batch);
                }

                // Flush if: batch is full, timeout reached, or shutting down
                long now = System.currentTimeMillis();
                boolean timeoutReached = (now - lastFlushTime) >= FLUSH_INTERVAL_MS;
                boolean batchFull = shouldFlushBatch(batch);
                boolean shuttingDown = !running.get();

                if (!batch.isEmpty() && (batchFull || timeoutReached || shuttingDown)) {
                    sendBatchToCloudWatch(batch);
                    batch.clear();
                    lastFlushTime = System.currentTimeMillis();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                LOGGER.error("Error processing CloudWatch events", e);
            }
        }

        // Final flush on shutdown
        if (!batch.isEmpty()) {
            sendBatchToCloudWatch(batch);
        }
    }

    /**
     * Drains additional events from the queue into the current batch up to the maximum batch size.
     *
     * @param batch the batch to add events to
     */
    private void drainQueueToBatch(List<InputLogEvent> batch) {
        while (batch.size() < MAX_BATCH_SIZE) {
            InputLogEvent event = eventQueue.poll();
            if (event == null) {
                break;
            }
            batch.add(event);
        }
    }

    /**
     * Determines whether a batch should be flushed based on size and byte limits.
     *
     * @param batch the batch to evaluate
     * @return true if the batch should be flushed, false otherwise
     */
    private boolean shouldFlushBatch(List<InputLogEvent> batch) {
        if (batch.isEmpty()) {
            return false;
        }

        if (batch.size() >= MAX_BATCH_SIZE) {
            return true;
        }

        long totalBytes = batch.stream()
                .mapToLong(e -> e.message().length())
                .sum();

        return totalBytes >= MAX_BATCH_BYTES;
    }

    /**
     * Sends a batch of log events to CloudWatch with automatic retry and error handling.
     *
     * @param events the log events to send
     */
    private void sendBatchToCloudWatch(List<InputLogEvent> events) {
        if (events.isEmpty()) {
            return;
        }

        // Sort by timestamp (CloudWatch requirement)
        events.sort(Comparator.comparing(InputLogEvent::timestamp));

        int retries = 0;
        int maxRetries = 3;
        long backoffMs = 100;

        while (retries < maxRetries) {
            try {
                PutLogEventsRequest.Builder requestBuilder = PutLogEventsRequest.builder()
                        .logGroupName(logGroupName)
                        .logStreamName(logStreamName)
                        .logEvents(events);

                String currentToken = sequenceToken.get();
                if (currentToken != null) {
                    requestBuilder.sequenceToken(currentToken);
                }

                PutLogEventsResponse response = cloudWatchClient.putLogEvents(requestBuilder.build());
                sequenceToken.set(response.nextSequenceToken());

                LOGGER.debug("Sent {} events to CloudWatch", events.size());
                return;

            } catch (InvalidSequenceTokenException e) {
                LOGGER.debug("Invalid sequence token, retrying with correct token");
                sequenceToken.set(e.expectedSequenceToken());
                retries++;

            } catch (DataAlreadyAcceptedException e) {
                LOGGER.debug("Data already accepted, updating sequence token");
                sequenceToken.set(e.expectedSequenceToken());
                return;

            } catch (ResourceNotFoundException e) {
                LOGGER.error("Log group or stream not found: {}/{}", logGroupName, logStreamName);
                initializeCloudWatchResources();
                retries++;

            } catch (SdkException e) {
                LOGGER.error("Error sending logs to CloudWatch (attempt {}/{})", retries + 1, maxRetries, e);
                retries++;

                if (retries < maxRetries) {
                    try {
                        Thread.sleep(backoffMs);
                        backoffMs *= 2; // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }

        LOGGER.error("Failed to send {} events to CloudWatch after {} retries", events.size(), maxRetries);
    }

    /**
     * Stops the appender and gracefully shuts down background processing.
     * Flushes remaining events and closes the CloudWatch client.
     */
    @Override
    public void stop() {
        LOGGER.info("Stopping CloudWatch appender for {}/{}", logGroupName, logStreamName);

        running.set(false);

        try {
            // Wait for writer to finish processing queue
            writerExecutor.shutdown();
            if (!writerExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                LOGGER.warn("CloudWatch writer did not terminate in time, forcing shutdown");
                writerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            writerExecutor.shutdownNow();
        }

        // Close CloudWatch client
        try {
            cloudWatchClient.close();
        } catch (Exception e) {
            LOGGER.error("Error closing CloudWatch client", e);
        }

        super.stop();
        LOGGER.info("CloudWatch appender stopped");
    }

    /**
     * Factory method for creating CloudWatchAppender instances from Log4j2 configuration.
     *
     * @param name            the appender name
     * @param logGroup        the CloudWatch log group name
     * @param logStream       the CloudWatch log stream name
     * @param region          the AWS region (default: eu-central-1)
     * @param endpoint        optional custom endpoint URL for LocalStack or other AWS-compatible services
     * @param accessKeyId     optional AWS access key ID
     * @param secretAccessKey optional AWS secret access key
     * @param createLogGroup  whether to create the log group if it doesn't exist (default: true)
     * @param createLogStream whether to create the log stream if it doesn't exist (default: true)
     * @param layout          the layout for formatting log messages
     * @param filter          the filter to apply to log events
     * @return configured CloudWatchAppender instance or null if configuration is invalid
     */
    @PluginFactory
    public static CloudWatchAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("logGroup") String logGroup,
            @PluginAttribute("logStream") String logStream,
            @PluginAttribute(value = "region", defaultString = "eu-central-1") String region,
            @PluginAttribute("endpoint") String endpoint,
            @PluginAttribute("accessKeyId") String accessKeyId,
            @PluginAttribute("secretAccessKey") String secretAccessKey,
            @PluginAttribute(value = "createLogGroup", defaultBoolean = true) boolean createLogGroup,
            @PluginAttribute(value = "createLogStream", defaultBoolean = true) boolean createLogStream,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {

        if (name == null) {
            LOGGER.error("No name provided for CloudWatchAppender");
            return null;
        }

        if (logGroup == null) {
            LOGGER.error("No logGroup provided for CloudWatchAppender");
            return null;
        }

        if (logStream == null) {
            LOGGER.error("No logStream provided for CloudWatchAppender");
            return null;
        }

        // Build CloudWatch client
        CloudWatchLogsClient client = buildCloudWatchClient(region, endpoint, accessKeyId, secretAccessKey);

        return new CloudWatchAppender(
                name,
                filter,
                layout,
                true,
                Property.EMPTY_ARRAY,
                client,
                logGroup,
                logStream,
                createLogGroup,
                createLogStream
        );
    }

    /**
     * Builds a configured CloudWatch Logs client with optional custom endpoint and credentials.
     *
     * @param region          the AWS region
     * @param endpoint        optional custom endpoint URL
     * @param accessKeyId     optional AWS access key ID
     * @param secretAccessKey optional AWS secret access key
     * @return configured CloudWatchLogsClient instance
     */
    private static CloudWatchLogsClient buildCloudWatchClient(
            String region,
            String endpoint,
            String accessKeyId,
            String secretAccessKey) {

        CloudWatchLogsClientBuilder builder = CloudWatchLogsClient.builder()
                .region(Region.of(region))
                .overrideConfiguration(config -> config
                        .apiCallTimeout(Duration.ofSeconds(30))
                        .apiCallAttemptTimeout(Duration.ofSeconds(10))
                );

        // Custom endpoint (LocalStack)
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        // Credentials
        AwsCredentialsProvider credentialsProvider;
        if (accessKeyId != null && secretAccessKey != null) {
            credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)
            );
        } else {
            credentialsProvider = DefaultCredentialsProvider.builder().build();
        }
        builder.credentialsProvider(credentialsProvider);

        return builder.build();
    }
}