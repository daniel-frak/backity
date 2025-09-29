package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backup.application.StorageSolutionWriteService;
import dev.codesoapbox.backity.core.backup.application.WriteDestination;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelFileCopyUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final StorageSolutionWriteService storageSolutionWriteService;

    public void cancelFileCopy(FileCopyId fileCopyId) {
        FileCopy fileCopy = fileCopyRepository.getById(fileCopyId);

        if (fileCopy.getStatus() == FileCopyStatus.IN_PROGRESS) {
            BackupTarget backupTarget = backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId());
            var writeDestination = new WriteDestination(
                    backupTarget.getStorageSolutionId(),
                    fileCopy.getFilePath()
            );
            storageSolutionWriteService.cancelWrite(writeDestination);
        } else if (fileCopy.getStatus() == FileCopyStatus.ENQUEUED) {
            fileCopy.toTracked();
            fileCopyRepository.save(fileCopy);
        }
    }
}
