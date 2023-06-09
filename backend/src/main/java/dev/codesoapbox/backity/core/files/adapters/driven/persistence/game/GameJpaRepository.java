package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameJpaRepository implements GameRepository {

    private JpaGameSpringRepository springRepository;

    @Override
    public void save(Game game) {

    }

    @Override
    public boolean existsByTitle(String title) {
        return false;
    }
}
