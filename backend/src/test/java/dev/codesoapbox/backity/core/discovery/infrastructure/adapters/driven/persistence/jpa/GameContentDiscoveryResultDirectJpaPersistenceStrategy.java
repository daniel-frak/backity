package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused")
public class GameContentDiscoveryResultDirectJpaPersistenceStrategy
        extends DirectJpaPersistenceStrategy<GameContentDiscoveryResult, GameContentDiscoveryResultJpaEntity> {

    public GameContentDiscoveryResultDirectJpaPersistenceStrategy(
            TestEntityManager entityManager,
            GameContentDiscoveryResultJpaEntityMapper entityMapper
    ) {
        super(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
                (em, obj) ->
                        em.find(GameContentDiscoveryResultJpaEntity.class, obj.getGameProviderId().value()),
                GameContentDiscoveryResult.class
        );
    }
}
