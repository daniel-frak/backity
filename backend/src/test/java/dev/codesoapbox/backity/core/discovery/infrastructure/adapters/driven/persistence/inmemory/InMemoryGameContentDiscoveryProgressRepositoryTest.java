package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.inmemory;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryGameContentDiscoveryProgressRepositoryTest {

    private InMemoryGameContentDiscoveryProgressRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGameContentDiscoveryProgressRepository();
    }

    @Test
    void shouldSave() {
        GameContentDiscoveryProgress progress = TestGameContentDiscoveryProgress.twentyFivePercentGog();

        repository.save(progress);

        List<GameContentDiscoveryProgress> foundProgresses =
                repository.findAllByGameProviderIdIn(List.of(progress.gameProviderId()));
        assertThat(foundProgresses).containsExactly(progress);
    }

    @Test
    void shouldDeleteByGameProviderId() {
        GameContentDiscoveryProgress progress = TestGameContentDiscoveryProgress.twentyFivePercentGog();
        repository.save(progress);

        repository.deleteByGameProviderId(progress.gameProviderId());

        List<GameContentDiscoveryProgress> foundProgresses =
                repository.findAllByGameProviderIdIn(List.of(progress.gameProviderId()));
        assertThat(foundProgresses).isEmpty();
    }

    @SuppressWarnings(
            // Content is identical to shouldSave, but the SUT method is different.
            // If shouldSave changes implementation in the future, we don't want to lose this test.
            "java:S4144")
    @Test
    void shouldFindAllByGameProviderIdIn() {
        GameContentDiscoveryProgress progress = TestGameContentDiscoveryProgress.twentyFivePercentGog();
        repository.save(progress);

        List<GameContentDiscoveryProgress> foundProgresses =
                repository.findAllByGameProviderIdIn(List.of(progress.gameProviderId()));

        assertThat(foundProgresses).containsExactly(progress);
    }
}