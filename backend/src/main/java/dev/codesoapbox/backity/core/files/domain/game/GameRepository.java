package dev.codesoapbox.backity.core.files.domain.game;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GameRepository {

    void save(Game game);

    Optional<Game> findByTitle(String title);

    Optional<Game> findById(GameId id);

    Page<Game> findAll(Pageable pageable);
}
