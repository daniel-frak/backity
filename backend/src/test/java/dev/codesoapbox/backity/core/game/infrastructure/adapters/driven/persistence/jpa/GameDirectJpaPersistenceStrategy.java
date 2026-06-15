package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused")
public class GameDirectJpaPersistenceStrategy extends DirectJpaPersistenceStrategy<Game, GameJpaEntity> {

    public GameDirectJpaPersistenceStrategy(
            TestEntityManager entityManager,
            GameJpaEntityMapper entityMapper
    ) {
        super(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
                (em, obj) -> em.find(GameJpaEntity.class, obj.getId().value()),
                Game.class
        );
    }
}
