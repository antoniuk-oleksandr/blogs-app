package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.repository.adapter.RevokedTokenRepositoryAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Configures and executes scheduled cleanup of expired revoked tokens.
 * Uses a cron expression from application configuration to determine cleanup frequency.
 */
@Component
public class RevokedTokenCleanerImpl implements RevokedTokenCleaner, SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RevokedTokenCleanerImpl.class);

    private final RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter;

    private final String cron;

    private final Clock clock;

    /**
     * Constructs a new revoked token cleaner with repository adapter and cron schedule.
     *
     * @param revokedTokenRepositoryAdapter adapter for accessing revoked token data
     * @param cron                          cron expression defining cleanup schedule (e.g., "0 0 2 * * *" for 2 AM daily)
     * @param clock                         the application clock used for cleanup timestamps
     */
    public RevokedTokenCleanerImpl(
            RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter,
            @Value("${revoked-token-cleaner.cron}") String cron,
            Clock clock
    ) {
        this.revokedTokenRepositoryAdapter = revokedTokenRepositoryAdapter;
        this.cron = cron;
        this.clock = clock;
    }

    /**
     * Configures the scheduled task with a dynamic cron trigger.
     * Registers the cleanup task to be executed according to the cron expression.
     *
     * @param taskRegistrar Spring's task registrar for scheduling configuration
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                this::cleanUpExpiredTokens,
                triggerContext -> new CronTrigger(cron).nextExecution(triggerContext)
        );
    }

    /**
     * Executes the cleanup operation by deleting all expired revoked tokens.
     * Called automatically by the Spring scheduler based on the configured cron expression.
     */
    @Override
    public void cleanUpExpiredTokens() {
        LocalDateTime now = LocalDateTime.now(clock).withNano(0);
        revokedTokenRepositoryAdapter.deleteExpiredTokens(now);
        log.info("token_cleanup_completed timestamp={}", now);
    }
}
