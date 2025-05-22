package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.RequiredArgsConstructor;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
public class DownloadFileCopyUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final StorageSolution storageSolution;

    public FileResource downloadFileCopy(FileCopyId fileCopyId) throws FileNotFoundException {
        FileCopy fileCopy = fileCopyRepository.getById(fileCopyId);
        String filePath = fileCopy.getFilePath();

        return storageSolution.getFileResource(filePath);
    }
}
