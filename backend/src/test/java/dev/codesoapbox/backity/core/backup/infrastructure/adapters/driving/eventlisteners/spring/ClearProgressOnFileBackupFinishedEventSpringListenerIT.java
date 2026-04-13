package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.ClearProgressOnFileBackupFinishedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import dev.codesoapbox.backity.testing.messaging.inmemory.InMemoryEventScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class ClearProgressOnFileBackupFinishedEventSpringListenerIT {

    @Autowired
    private ClearProgressOnFileBackupFinishedEventHandler eventHandler;

    @Test
    void shouldHandleEvent(InMemoryEventScenario scenario) {
        FileBackupFinishedEvent event = TestFileBackupEvent.finishedIntegrityUnknown();

        scenario.publish(event)
                .thenVerifyAsync(() -> verify(eventHandler).handle(event));
    }
}