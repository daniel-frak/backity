package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import dev.codesoapbox.backity.testing.messaging.inmemory.InMemoryEventScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventSpringListenerIT {

    @Autowired
    private SaveProgressToRepositoryOnFileCopyReplicationProgressChangedEventHandler eventHandler;

    @Test
    void shouldHandle(InMemoryEventScenario scenario) {
        FileCopyReplicationProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        scenario.publish(event)
                .thenVerifyAsync(() -> verify(eventHandler).handle(event));
    }
}