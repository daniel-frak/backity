package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
abstract class GameContentDiscoveryResultJpaRepositoryAbstractIT {

    @Autowired
    protected GameContentDiscoveryResultJpaRepository repository;

    @Autowired
    protected GameContentDiscoveryResultSpringRepository springRepository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected GameContentDiscoveryResultJpaEntityMapper entityMapper;

    @BeforeEach
    void setUp() {
        persistExistingDependencies();
    }

    private void persistExistingDependencies() {
        for (GameContentDiscoveryResult discoveryResult : EXISTING_DISCOVERY_RESULTS.getAll()) {
            entityManager.persist(entityMapper.toEntity(discoveryResult));
        }
    }

    @Test
    void saveShouldSaveNew() {
        GameContentDiscoveryResult newDiscoveryResult = TestGameContentDiscoveryResult.gogBuilder()
                .withGameProviderId(new GameProviderId("ANOTHER_PROVIDER"))
                .build();

        repository.save(newDiscoveryResult);
        entityManager.flush();

        assertWasPersisted(newDiscoveryResult);
    }

    private void assertWasPersisted(GameContentDiscoveryResult newDiscoveryResult) {
        GameContentDiscoveryResult persistedDomain =
                getPersistedDiscoveryResult(newDiscoveryResult.getGameProviderId());
        assertSame(persistedDomain, newDiscoveryResult);
    }

    private GameContentDiscoveryResult getPersistedDiscoveryResult(GameProviderId gameProviderId) {
        GameContentDiscoveryResultJpaEntity persistedEntity = entityManager.find(
                GameContentDiscoveryResultJpaEntity.class, gameProviderId.value());
        return entityMapper.toDomain(persistedEntity);
    }

    private void assertSame(GameContentDiscoveryResult actual, GameContentDiscoveryResult expected) {
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void saveShouldReplaceExisting() {
        GameContentDiscoveryResult discoveryResult = EXISTING_DISCOVERY_RESULTS.GOG_DISCOVERY_RESULT.get();
        discoveryResult.setGamesDiscovered(999);
        repository.save(discoveryResult);
        entityManager.flush();

        assertDidNotCreateNewDbRow();
        GameContentDiscoveryResult persistedResult =
                getPersistedDiscoveryResult(discoveryResult.getGameProviderId());
        assertThat(persistedResult.getGamesDiscovered()).isEqualTo(999);
    }

    private void assertDidNotCreateNewDbRow() {
        long numberOfRecordsInDb = springRepository.count();
        assertThat(numberOfRecordsInDb).isEqualTo(EXISTING_DISCOVERY_RESULTS.getAll().size());
    }

    @Test
    void shouldFindAllByGameProviderIdIn() {
        GameContentDiscoveryResult expectedDiscoveryResult = EXISTING_DISCOVERY_RESULTS.GOG_DISCOVERY_RESULT.get();

        List<GameContentDiscoveryResult> result = repository.findAllByGameProviderIdIn(
                List.of(expectedDiscoveryResult.getGameProviderId()));

        assertThat(result).containsExactly(expectedDiscoveryResult);
    }

    private static class EXISTING_DISCOVERY_RESULTS {

        public static final Supplier<GameContentDiscoveryResult> GOG_DISCOVERY_RESULT =
                TestGameContentDiscoveryResult::gog;

        public static List<GameContentDiscoveryResult> getAll() {
            return List.of(GOG_DISCOVERY_RESULT.get());
        }
    }
}