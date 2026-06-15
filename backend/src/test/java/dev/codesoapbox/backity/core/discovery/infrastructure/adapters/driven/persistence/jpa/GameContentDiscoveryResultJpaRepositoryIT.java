package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryResult;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceAdapter;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class GameContentDiscoveryResultJpaRepositoryIT {

    @Autowired
    protected GameContentDiscoveryResultJpaRepository repository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    private DirectJpaPersistenceAdapter directPersistenceAdapter;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
        persistSampleDependencies();
    }

    private void persistSampleDependencies() {
        directPersistenceAdapter.persist(SampleDiscoveryResults.getAll());
    }

    @Test
    void saveShouldPersistNew() {
        GameContentDiscoveryResult newDiscoveryResult = TestGameContentDiscoveryResult.gogBuilder()
                .withGameProviderId(new GameProviderId("ANOTHER_PROVIDER"))
                .build();

        repository.save(newDiscoveryResult);
        entityManager.flush();

        GameContentDiscoveryResult persistedAggregate =
                directPersistenceAdapter.getPersistedDomainObject(newDiscoveryResult);
        assertThat(persistedAggregate)
                .usingRecursiveComparison()
                .isEqualTo(newDiscoveryResult);
    }

    @Test
    void saveShouldReplaceExisting() {
        GameContentDiscoveryResult discoveryResult = SampleDiscoveryResults.GOG_DISCOVERY_RESULT.get();
        discoveryResult.setGamesDiscovered(999);
        repository.save(discoveryResult);
        entityManager.flush();

        GameContentDiscoveryResult persistedResult =
                directPersistenceAdapter.getPersistedDomainObject(discoveryResult);
        assertThat(persistedResult.getGamesDiscovered()).isEqualTo(999);
    }

    @Test
    void findAllByGameProviderIdInShouldReturnAllDataForAggregate() {
        GameContentDiscoveryResult expectedDiscoveryResult = SampleDiscoveryResults.GOG_DISCOVERY_RESULT.get();

        List<GameContentDiscoveryResult> result = repository.findAllByGameProviderIdIn(
                List.of(expectedDiscoveryResult.getGameProviderId()));

        assertThat(result).containsExactly(expectedDiscoveryResult);
    }

    private static class SampleDiscoveryResults {

        public static final Supplier<GameContentDiscoveryResult> GOG_DISCOVERY_RESULT =
                TestGameContentDiscoveryResult::gog;

        public static List<GameContentDiscoveryResult> getAll() {
            return List.of(GOG_DISCOVERY_RESULT.get());
        }
    }
}