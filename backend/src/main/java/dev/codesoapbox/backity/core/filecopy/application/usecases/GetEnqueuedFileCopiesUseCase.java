package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetEnqueuedFileCopiesUseCase {

    private final FileCopyRepository fileCopyRepository;

    public Page<FileCopy> getEnqueuedFileCopies(Pagination pagination) {
        return fileCopyRepository.findAllEnqueued(pagination);
    }
}
