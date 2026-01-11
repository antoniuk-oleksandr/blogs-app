package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.repository.adapter.RevokedTokenRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevokedTokenCleanerImplTest {

    @Mock
    private RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter;

    @Mock
    private ScheduledTaskRegistrar taskRegistrar;

    private RevokedTokenCleanerImpl revokedTokenCleaner;

    @BeforeEach
    void setUp() {
        String cron = "0 0 * * * *";
        revokedTokenCleaner = new RevokedTokenCleanerImpl(revokedTokenRepositoryAdapter, cron);
    }

    @Test
    void cleanUpExpiredTokens_shouldInvokeRepositoryMethodSuccessfully() {
        revokedTokenCleaner.cleanUpExpiredTokens();

        verify(revokedTokenRepositoryAdapter).deleteExpiredTokens(any(LocalDateTime.class));
    }

    @Test
    void configureTasks_shouldRegisterScheduledTaskSuccessfully() {
        revokedTokenCleaner.configureTasks(taskRegistrar);

        verify(taskRegistrar).addTriggerTask(any(Runnable.class), any(Trigger.class));
    }

    @Test
    void configureTasks_shouldRegisterCronTrigger_andTriggerShouldComputeNextExecution() {
        revokedTokenCleaner.configureTasks(taskRegistrar);

        ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);
        verify(taskRegistrar).addTriggerTask(any(Runnable.class), triggerCaptor.capture());

        Trigger trigger = triggerCaptor.getValue();

        TriggerContext triggerContext = new SimpleTriggerContext();
        Instant nextExecution = trigger.nextExecution(triggerContext);

        assertThat(nextExecution).isNotNull();
        assertThat(nextExecution).isAfter(Instant.now());
    }
}
