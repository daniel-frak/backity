package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProcess;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class BackupRecoveryCompletedEventSpringListener {

    private final FileCopyReplicationProcess fileCopyReplicationProcess;

    @EventListener(BackupRecoveryCompletedEvent.class)
    public void handle() {
        fileCopyReplicationProcess.markBackupRecoveryCompleted();
    }
}
