package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.application.FileCopyWithContextFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetFileCopyQueueUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final FileCopyWithContextFactory fileCopyWithContextFactory;

    public Page<FileCopyWithContext> getFileCopyQueue(Pagination pagination) {
        Page<FileCopy> fileCopiesInQueue = fileCopyRepository.findAllInProgressOrEnqueued(pagination);

        if (fileCopiesInQueue.content().isEmpty()) {
            return fileCopiesInQueue.asEmpty();
        }

        return fileCopyWithContextFactory.createPageFrom(fileCopiesInQueue);
    }
}
