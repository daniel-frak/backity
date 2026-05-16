package dev.codesoapbox.backity.core.game.domain;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GameRepository {

    void save(Game game);

    Optional<Game> findByTitle(GameTitle title);

    Optional<Game> findById(GameId id);

    Game getById(GameId gameId);

    Page<Game> findAll(Pagination pagination);

    List<Game> findAllByIdIn(Collection<GameId> ids);
}
