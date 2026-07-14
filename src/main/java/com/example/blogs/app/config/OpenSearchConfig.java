package com.example.blogs.app.config;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.BasicHeader;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5Transport;
import org.opensearch.client.transport.httpclient5.internal.NodeSelector;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenSearchConfig {

    private final String host;

    private final int port;

    private final String password;

    private final String username;

    public OpenSearchConfig(
            @Value("${opensearch.host}") String host,
            @Value("${opensearch.port}") int port,
            @Value("${opensearch.password}") String password,
            @Value("${opensearch.username}") String username
    ) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.username = username;
    }

    @Bean
    public OpenSearchClient openSearchClient() {
        URI hostUri = URI.create(host);
        String scheme = hostUri.getScheme() == null ? "http" : hostUri.getScheme();
        String hostname = hostUri.getHost() == null ? host : hostUri.getHost();

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(hostname, port),
                new UsernamePasswordCredentials(username, password.toCharArray())
        );

        CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
        httpClient.start();

        List<Header> defaultHeaders = new ArrayList<>();
        defaultHeaders.add(new BasicHeader("Accept", "application/json"));

        List<org.opensearch.client.transport.httpclient5.internal.Node> nodes = List.of(
                new org.opensearch.client.transport.httpclient5.internal.Node(
                        new HttpHost(scheme, hostname, port)
                )
        );

        return new OpenSearchClient(
                new ApacheHttpClient5Transport(
                        httpClient,
                        defaultHeaders.toArray(new Header[0]),
                        nodes,
                        new JacksonJsonpMapper(),
                        null,
                        "",
                        new ApacheHttpClient5Transport.FailureListener(),
                        NodeSelector.ANY,
                        false,
                        false,
                        false
                )
        );
    }
}
