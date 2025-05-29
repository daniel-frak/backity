package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteFileCopyUseCase {

    private final StorageSolution storageSolution;
    private final FileCopyRepository fileCopyRepository;

    public void deleteFileCopy(FileCopyId fileCopyId) {
        FileCopy fileCopy = fileCopyRepository.getById(fileCopyId);
        fileCopy.validateIsBackedUp();

        deleteFileCopy(fileCopy);
        fileCopy.toTracked();
        fileCopyRepository.save(fileCopy);
    }

    private void deleteFileCopy(FileCopy fileCopy) {
        String filePath = fileCopy.getFilePath();
        storageSolution.deleteIfExists(filePath);
    }
}
