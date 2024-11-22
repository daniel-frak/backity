package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileUseCase {

    private final GameFileRepository gameFileRepository;

    public Optional<GameFile> findCurrentlyDownloadingFile() {
        return gameFileRepository.findCurrentlyDownloading();
    }
}
