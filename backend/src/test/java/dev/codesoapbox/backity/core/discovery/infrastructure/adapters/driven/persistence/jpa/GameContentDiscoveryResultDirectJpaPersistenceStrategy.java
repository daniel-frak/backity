package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@SuppressWarnings("unused") // Used via component scanning
@RequiredArgsConstructor
public class GameContentDiscoveryResultDirectJpaPersistenceStrategy
        implements DirectJpaPersistenceStrategy<GameContentDiscoveryResult, GameContentDiscoveryResultJpaEntity> {

    private final GameContentDiscoveryResultJpaEntityMapper entityMapper;

    @Override
    public Class<GameContentDiscoveryResult> getDomainObjectClass() {
        return GameContentDiscoveryResult.class;
    }

    @Override
    public GameContentDiscoveryResultJpaEntity toEntity(GameContentDiscoveryResult domainObject) {
        return entityMapper.toEntity(domainObject);
    }

    @Override
    public GameContentDiscoveryResult toDomain(GameContentDiscoveryResultJpaEntity entity) {
        return entityMapper.toDomain(entity);
    }

    @Override
    public GameContentDiscoveryResultJpaEntity findPersistedEntity(
            TestEntityManager entityManager, GameContentDiscoveryResult domainObject) {
        return entityManager.find(
                GameContentDiscoveryResultJpaEntity.class,
                domainObject.getGameProviderId().value()
        );
    }
}
