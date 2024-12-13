package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetEnqueuedFileListUseCase {

    private final GameFileRepository gameFileRepository;

    public Page<GameFile> getEnqueuedFileList(Pagination pagination) {
        return gameFileRepository.findAllWaitingForDownload(pagination);
    }
}
