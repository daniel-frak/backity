package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileBackupStartedEventHandlerTest {

    @Mock
    private FileBackupStartedEventExternalForwarder eventForwarder;

    private FileBackupStartedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new FileBackupStartedEventHandler(eventForwarder);
    }

    @Test
    void shouldHandleEvent() {
        FileBackupStartedEvent event = TestFileBackupEvent.started();

        eventHandler.handle(event);

        verify(eventForwarder).forward(event);
    }
}