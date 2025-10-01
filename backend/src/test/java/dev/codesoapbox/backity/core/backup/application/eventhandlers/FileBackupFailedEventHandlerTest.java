package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileBackupFailedEventHandlerTest {

    @Mock
    private FileBackupFailedEventExternalForwarder eventForwarder;

    private FileBackupFailedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new FileBackupFailedEventHandler(eventForwarder);
    }

    @Test
    void shouldHandleEvent() {
        FileBackupFailedEvent event = TestFileBackupEvent.failed();

        eventHandler.handle(event);

        verify(eventForwarder).forward(event);
    }
}