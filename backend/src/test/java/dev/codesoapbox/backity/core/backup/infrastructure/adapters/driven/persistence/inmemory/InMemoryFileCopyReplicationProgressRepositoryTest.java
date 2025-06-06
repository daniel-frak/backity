package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.inmemory;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backup.domain.TestFileCopyReplicationProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryFileCopyReplicationProgressRepositoryTest {

    private InMemoryFileCopyReplicationProgressRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFileCopyReplicationProgressRepository();
    }

    @Test
    void shouldSave() {
        FileCopyReplicationProgress progress = TestFileCopyReplicationProgress.twentyFivePercent();

        repository.save(progress);

        List<FileCopyReplicationProgress> foundProgresses =
                repository.findAllByFileCopyIdIn(List.of(progress.fileCopyId()));
        assertThat(foundProgresses).containsExactly(progress);
    }

    @Test
    void shouldDeleteByFileCopyId() {
        FileCopyReplicationProgress progress = TestFileCopyReplicationProgress.twentyFivePercent();
        repository.save(progress);

        repository.deleteByFileCopyId(progress.fileCopyId());

        List<FileCopyReplicationProgress> foundProgresses =
                repository.findAllByFileCopyIdIn(List.of(progress.fileCopyId()));
        assertThat(foundProgresses).isEmpty();
    }

    @SuppressWarnings(
            // Content is identical to shouldSave, but the SUT method is different.
            // If shouldSave changes implementation in the future, we don't want to lose this test.
            "java:S4144")
    @Test
    void shouldFindAllByFileCopyIdIn() {
        FileCopyReplicationProgress progress = TestFileCopyReplicationProgress.twentyFivePercent();
        repository.save(progress);

        List<FileCopyReplicationProgress> foundProgresses =
                repository.findAllByFileCopyIdIn(List.of(progress.fileCopyId()));

        assertThat(foundProgresses).containsExactly(progress);
    }
}