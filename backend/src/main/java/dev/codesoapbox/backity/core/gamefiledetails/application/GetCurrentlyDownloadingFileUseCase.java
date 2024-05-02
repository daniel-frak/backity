package dev.codesoapbox.backity.core.gamefiledetails.application;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileUseCase {

    private final GameFileDetailsRepository gameFileDetailsRepository;

    public Optional<GameFileDetails> findCurrentlyDownloadingFile() {
        return gameFileDetailsRepository.findCurrentlyDownloading();
    }
}
