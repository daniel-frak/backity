package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EditBackupTargetUseCase {

    private final BackupTargetRepository backupTargetRepository;

    public void editBackupTarget(EditBackupTargetCommand command) {
        // @TODO
    }
}
