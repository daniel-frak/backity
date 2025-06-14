package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelFileCopyUseCase {

    private final FileCopyRepository fileCopyRepository;

    public void cancelFileCopy(FileCopyId fileCopyId) {
        FileCopy fileCopy = fileCopyRepository.getById(fileCopyId);
        fileCopy.toTracked();
        fileCopyRepository.save(fileCopy);
    }
}
