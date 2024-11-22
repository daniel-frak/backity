package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetProcessedFileListUseCase {

    private final GameFileRepository gameFileRepository;

    public Page<GameFile> getProcessedFileList(Pagination pagination) {
        return gameFileRepository.findAllProcessed(pagination);
    }
}
