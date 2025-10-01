package dev.codesoapbox.backity.core.backup.application.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BackupRecoveryCompletedEventHandler {

    private final FileCopyReplicationProcess fileCopyReplicationProcess;

    public void handle() {
        fileCopyReplicationProcess.markBackupRecoveryCompleted();
    }
}
