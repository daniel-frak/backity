package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused") // Used via component scanning
@RequiredArgsConstructor
public class GameDirectJpaPersistenceStrategy implements DirectJpaPersistenceStrategy<Game, GameJpaEntity> {

    private final GameJpaEntityMapper entityMapper;

    @Override
    public Class<Game> getDomainObjectClass() {
        return Game.class;
    }

    @Override
    public GameJpaEntity toEntity(Game domainObject) {
        return entityMapper.toEntity(domainObject);
    }

    @Override
    public Game toDomain(GameJpaEntity entity) {
        return entityMapper.toDomain(entity);
    }

    @Override
    public GameJpaEntity findPersistedEntity(TestEntityManager entityManager, Game domainObject) {
        return entityManager.find(
                GameJpaEntity.class,
                domainObject.getId().value()
        );
    }
}
