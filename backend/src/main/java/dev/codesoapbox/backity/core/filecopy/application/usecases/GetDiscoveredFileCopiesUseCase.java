package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

// @TODO Remove this
@RequiredArgsConstructor
public class GetDiscoveredFileCopiesUseCase {

    private final FileCopyRepository fileCopyRepository;

    public Page<FileCopy> getDiscoveredFileCopies(Pagination pagination) {
        return fileCopyRepository.findAllDiscovered(pagination);
    }
}
