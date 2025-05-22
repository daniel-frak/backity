package dev.codesoapbox.backity.core.game.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.game.application.GameFileWithCopies;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetGamesWithFilesUseCase {

    private final GameRepository gameRepository;
    private final GameFileRepository gameFileRepository;
    private final FileCopyRepository fileCopyRepository;

    public Page<GameWithFileCopies> getGamesWithFiles(Pagination pagination) {
        Page<Game> games = gameRepository.findAll(pagination);
        return games.map(this::findFilesWithCopies);
    }

    private GameWithFileCopies findFilesWithCopies(Game game) {
        List<GameFile> gameFiles = gameFileRepository.findAllByGameId(game.getId());
        List<GameFileWithCopies> gameFilesWithCopies = gameFiles.stream()
                .map(this::addFileCopies)
                .toList();
        return new GameWithFileCopies(game, gameFilesWithCopies);
    }

    private GameFileWithCopies addFileCopies(GameFile gameFile) {
        List<FileCopy> fileCopies = fileCopyRepository.findAllByGameFileId(gameFile.getId());
        return new GameFileWithCopies(gameFile, fileCopies);
    }
}
