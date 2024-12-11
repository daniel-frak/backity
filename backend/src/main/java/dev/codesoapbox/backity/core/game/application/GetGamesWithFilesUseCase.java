package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetGamesWithFilesUseCase {

    private final GameRepository gameRepository;
    private final GameFileRepository gameFileRepository;

    public Page<GameWithFiles> getGamesWithFiles(Pagination pagination) {
        Page<Game> games = gameRepository.findAll(pagination);
        return games.map(this::findFiles);
    }

    private GameWithFiles findFiles(Game game) {
        List<GameFile> gameFiles = gameFileRepository.findAllByGameId(game.getId());
        return new GameWithFiles(game, gameFiles);
    }
}
