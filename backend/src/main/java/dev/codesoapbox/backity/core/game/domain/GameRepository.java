package dev.codesoapbox.backity.core.game.domain;

import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;

import java.util.Optional;

public interface GameRepository {

    void save(Game game);

    Optional<Game> findByTitle(String title);

    Optional<Game> findById(GameId id);

    Page<Game> findAll(Pagination pagination);
}
