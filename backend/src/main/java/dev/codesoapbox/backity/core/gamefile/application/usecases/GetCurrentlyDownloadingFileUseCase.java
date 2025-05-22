package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final GameFileRepository gameFileRepository;

    public Optional<GameFile> findCurrentlyDownloadingFile() {
        return fileCopyRepository.findCurrentlyDownloading()
                .map(fileCopy -> gameFileRepository.getById(fileCopy.getGameFileId()));
    }
}
