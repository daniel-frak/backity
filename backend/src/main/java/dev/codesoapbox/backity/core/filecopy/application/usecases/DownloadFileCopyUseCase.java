package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import lombok.RequiredArgsConstructor;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
public class DownloadFileCopyUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final StorageSolutionRepository storageSolutionRepository;

    public FileResource downloadFileCopy(FileCopyId fileCopyId) throws FileNotFoundException {
        FileCopy fileCopy = fileCopyRepository.getById(fileCopyId);
        String filePath = fileCopy.getFilePath();
        StorageSolution storageSolution = getStorageSolution(fileCopy);

        return storageSolution.getFileResource(filePath);
    }

    private StorageSolution getStorageSolution(FileCopy fileCopy) {
        BackupTarget backupTarget = backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId());
        return storageSolutionRepository.getById(backupTarget.getStorageSolutionId());
    }
}
