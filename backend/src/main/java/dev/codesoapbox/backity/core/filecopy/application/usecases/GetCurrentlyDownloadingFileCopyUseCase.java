package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileCopyUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final GameFileRepository gameFileRepository;
    private final GameRepository gameRepository;

    public Optional<FileCopyWithContext> findCurrentlyDownloadingFileCopy() {
        return fileCopyRepository.findCurrentlyDownloading()
                .map(this::addContext);
    }

    private FileCopyWithContext addContext(FileCopy fileCopy) {
        GameFile gameFile = gameFileRepository.getById(fileCopy.getNaturalId().gameFileId());
        Game game = gameRepository.getById(gameFile.getGameId());

        return new FileCopyWithContext(fileCopy, gameFile, game);
    }
}
