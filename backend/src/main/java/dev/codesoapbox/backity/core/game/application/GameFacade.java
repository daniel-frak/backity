package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GameFacade {

    private final GameRepository gameRepository;
    private final FileDetailsRepository fileDetailsRepository;

    public Page<GameWithFiles> getGamesWithFiles(Pagination pagination) {
        Page<Game> games = gameRepository.findAll(pagination);
        return games.map(this::findFiles);
    }

    private GameWithFiles findFiles(Game game) {
        List<FileDetails> fileDetails = fileDetailsRepository.findAllByGameId(game.getId());
        return new GameWithFiles(game, fileDetails);
    }
}
