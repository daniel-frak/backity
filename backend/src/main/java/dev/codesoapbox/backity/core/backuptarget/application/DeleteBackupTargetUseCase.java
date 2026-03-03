package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteBackupTargetUseCase {

    private final BackupTargetRepository backupTargetRepository;

    public void editBackupTarget(BackupTargetId id) {
        // @TODO
    }
}
