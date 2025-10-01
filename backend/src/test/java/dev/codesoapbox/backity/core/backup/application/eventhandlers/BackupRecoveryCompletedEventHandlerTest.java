package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BackupRecoveryCompletedEventHandlerTest {

    @Mock
    private FileCopyReplicationProcess fileCopyReplicationProcess;

    private BackupRecoveryCompletedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new BackupRecoveryCompletedEventHandler(fileCopyReplicationProcess);
    }

    @Test
    void shouldHandleEvent() {
        eventHandler.handle();

        verify(fileCopyReplicationProcess).markBackupRecoveryCompleted();
    }
}