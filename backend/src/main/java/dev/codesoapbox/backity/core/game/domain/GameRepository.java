package dev.codesoapbox.backity.core.game.domain;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.NonNull;

import java.util.Optional;

public interface GameRepository {

    Game save(Game game);

    Optional<Game> findByTitle(String title);

    Optional<Game> findById(GameId id);

    Game getById(GameId gameId);

    Page<Game> findAll(Pagination pagination);
}
