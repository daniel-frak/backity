package dev.codesoapbox.backity.core.game.domain;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;

import java.util.*;

public interface GameRepository {

    Game save(Game game);

    Optional<Game> findByTitle(String title);

    Optional<Game> findById(GameId id);

    Game getById(GameId gameId);

    Page<Game> findAll(Pagination pagination);

    List<Game> findAllByIdIn(Collection<GameId> ids);
}
