package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetIsInUseException;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteBackupTargetUseCase {

    private final BackupTargetRepository backupTargetRepository;
    private final FileCopyRepository fileCopyRepository;

    public void deleteBackupTarget(BackupTargetId id) {
        if (fileCopyRepository.existByBackupTargetIdAndStatusNotIn(id, FileCopyStatus.NON_LOCKING_STATUSES)) {
            throw new BackupTargetIsInUseException(id);
        }
        // Delete by non-locking status only so that we don't accidentally delete a FileCopy that was just backed up:
        fileCopyRepository.deleteByBackupTargetIdAndStatusIn(id, FileCopyStatus.NON_LOCKING_STATUSES);
        backupTargetRepository.deleteById(id);
    }
}
