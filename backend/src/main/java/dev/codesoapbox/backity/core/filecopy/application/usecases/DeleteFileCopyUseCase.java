package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteFileCopyUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final StorageSolutionRepository storageSolutionRepository;

    public void deleteFileCopy(FileCopyId fileCopyId) {
        FileCopy fileCopy = fileCopyRepository.getById(fileCopyId);
        if(!fileCopy.isStored()) {
            throw new FileCopyNotBackedUpException(fileCopyId);
        }

        deleteFileCopy(fileCopy);
        fileCopy.toTracked();
        fileCopyRepository.save(fileCopy);
    }

    private void deleteFileCopy(FileCopy fileCopy) {
        String filePath = fileCopy.getFilePath();
        StorageSolution storageSolution = getStorageSolution(fileCopy);
        storageSolution.deleteIfExists(filePath);
    }

    private StorageSolution getStorageSolution(FileCopy fileCopy) {
        BackupTarget backupTarget = backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId());
        return storageSolutionRepository.getById(backupTarget.getStorageSolutionId());
    }
}
