package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import dev.codesoapbox.backity.testing.messaging.inmemory.InMemoryEventScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventSpringListenerIT {

    @Autowired
    private MarkBackupRecoveryCompletedOnBackupRecoveryCompletedEventHandler eventHandler;

    @Test
    void shouldHandleEvent(InMemoryEventScenario scenario) {
        var event = new BackupRecoveryCompletedEvent();

        scenario.publish(event)
                .thenVerifyAsync(() -> verify(eventHandler).handle());
    }
}