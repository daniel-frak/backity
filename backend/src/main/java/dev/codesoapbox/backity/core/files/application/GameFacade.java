package dev.codesoapbox.backity.core.files.application;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionRepository;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class GameFacade {

    private final GameRepository gameRepository;
    private final GameFileVersionRepository gameFileRepository;

    public Page<GameWithFiles> getGamesWithFiles(Pageable pageable) {
        Page<Game> games = gameRepository.findAll(pageable);
        return games.map(this::findFiles);
    }

    private GameWithFiles findFiles(Game game) {
        List<GameFileVersion> gameFileVersions = gameFileRepository.findAllByGameId(game.getId());
        return new GameWithFiles(game, gameFileVersions);
    }
}
