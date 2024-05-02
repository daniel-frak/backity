package dev.codesoapbox.backity.core.gamefiledetails.application;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetEnqueuedFileListUseCase {

    private final GameFileDetailsRepository gameFileDetailsRepository;

    public Page<GameFileDetails> getEnqueuedFileList(Pagination pagination) {
        return gameFileDetailsRepository.findAllWaitingForDownload(pagination);
    }
}
