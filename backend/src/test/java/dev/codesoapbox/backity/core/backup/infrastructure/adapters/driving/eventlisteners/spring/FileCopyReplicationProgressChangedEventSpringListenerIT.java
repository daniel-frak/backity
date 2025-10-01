package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileCopyReplicationProgressChangedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class FileCopyReplicationProgressChangedEventSpringListenerIT {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private FileCopyReplicationProgressChangedEventHandler eventHandler;

    @Test
    void shouldHandle() {
        FileCopyReplicationProgressChangedEvent event = TestFileBackupEvent.progressChanged();

        applicationEventPublisher.publishEvent(event);

        verify(eventHandler).handle(event);
    }
}