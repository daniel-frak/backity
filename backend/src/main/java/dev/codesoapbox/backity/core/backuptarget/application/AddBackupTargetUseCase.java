package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddBackupTargetUseCase {

    private final BackupTargetRepository backupTargetRepository;

    public void addBackupTarget(AddBackupTargetCommand command) {
        // @TODO
    }
}
