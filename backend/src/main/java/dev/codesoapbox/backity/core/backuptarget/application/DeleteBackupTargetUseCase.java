package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetIsInUseException;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteBackupTargetUseCase {

    private final BackupTargetRepository backupTargetRepository;
    private final FileCopyRepository fileCopyRepository;

    public void deleteBackupTarget(BackupTargetId id) {
        if(fileCopyRepository.existByBackupTargetId(id)) {
            throw new BackupTargetIsInUseException(id);
        }
        backupTargetRepository.deleteById(id);
    }
}
