package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.inmemory.eventlisteners;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class BackupRecoveryCompletedEventListener {

    private final FileCopyReplicationProcess fileCopyReplicationProcess;

    @EventListener(BackupRecoveryCompletedEvent.class)
    public void handle() {
        fileCopyReplicationProcess.markBackupRecoveryCompleted();
    }
}
