package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddBackupTargetUseCase {

    private final BackupTargetRepository backupTargetRepository;

    public BackupTarget addBackupTarget(AddBackupTargetCommand command) {
        BackupTarget backupTarget = create(command);
        backupTargetRepository.save(backupTarget);

        return backupTarget;
    }

    private BackupTarget create(AddBackupTargetCommand command) {
        return BackupTarget.create(
                command.name(),
                command.storageSolutionId(),
                command.pathTemplate()
        );
    }
}
