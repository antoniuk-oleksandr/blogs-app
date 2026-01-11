package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.repository.adapter.RevokedTokenRepositoryAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RevokedTokenCleanerImpl implements RevokedTokenCleaner, SchedulingConfigurer {

    private final RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter;

    private final String cron;

    public RevokedTokenCleanerImpl(
            RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter,
            @Value("${revoked-token-cleaner.cron}") String cron
    ) {
        this.revokedTokenRepositoryAdapter = revokedTokenRepositoryAdapter;
        this.cron = cron;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                this::cleanUpExpiredTokens,
                triggerContext -> new CronTrigger(cron).nextExecution(triggerContext)
        );
    }

    @Override
    public void cleanUpExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        revokedTokenRepositoryAdapter.deleteExpiredTokens(now);
    }
}
