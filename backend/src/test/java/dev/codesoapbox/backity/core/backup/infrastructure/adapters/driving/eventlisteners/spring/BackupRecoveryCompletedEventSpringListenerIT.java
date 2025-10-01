package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.BackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class BackupRecoveryCompletedEventSpringListenerIT {

    @Autowired
    private BackupRecoveryCompletedEventHandler eventHandler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldHandleEvent() {
        var event = new BackupRecoveryCompletedEvent();

        applicationEventPublisher.publishEvent(event);

        verify(eventHandler).handle();
    }
}