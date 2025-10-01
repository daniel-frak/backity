package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupStartedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class FileBackupStartedEventSpringListenerIT {

    @Autowired
    private FileBackupStartedEventHandler eventHandler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldPublishWebSocketEvent() {
        FileBackupStartedEvent event = TestFileBackupEvent.started();

        applicationEventPublisher.publishEvent(event);

        verify(eventHandler).handle(event);
    }
}