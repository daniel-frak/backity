package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFailedEventHandler;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class FileBackupFailedEventSpringListenerIT {

    @Autowired
    private FileBackupFailedEventHandler eventHandler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldPublishWebSocketEvent() {
        FileBackupFailedEvent event = TestFileBackupEvent.failed();

        applicationEventPublisher.publishEvent(event);

        verify(eventHandler).handle(event);
    }
}