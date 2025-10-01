package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileBackupFinishedEventHandlerTest {

    @Mock
    private FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository;

    @Mock
    private FileBackupFinishedEventExternalForwarder eventForwarder;

    private FileBackupFinishedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new FileBackupFinishedEventHandler(fileCopyReplicationProgressRepository, eventForwarder);
    }

    @Test
    void shouldHandleEvent() {
        FileBackupFinishedEvent event = TestFileBackupEvent.finishedIntegrityUnknown();

        eventHandler.handle(event);

        verify(fileCopyReplicationProgressRepository).deleteByFileCopyId(event.fileCopyId());
        verify(eventForwarder).forward(event);
    }
}