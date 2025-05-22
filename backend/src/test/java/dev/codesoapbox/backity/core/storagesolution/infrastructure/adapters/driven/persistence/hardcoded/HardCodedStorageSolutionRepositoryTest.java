package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.persistence.hardcoded;

import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.StorageSolutionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HardCodedStorageSolutionRepositoryTest {

    private FakeUnixStorageSolution fakeUnixStorageSolution;
    private HardCodedStorageSolutionRepository repository;

    @BeforeEach
    void setUp() {
        fakeUnixStorageSolution = new FakeUnixStorageSolution();
        repository = new HardCodedStorageSolutionRepository(List.of(fakeUnixStorageSolution));
    }

    @Test
    void shouldGetById() {
        StorageSolution result = repository.getById(FakeUnixStorageSolution.ID);

        assertThat(result).isEqualTo(fakeUnixStorageSolution);
    }

    @Test
    void getByIdShouldThrowExceptionGivenNotFound() {
        StorageSolutionId nonExistentId = new StorageSolutionId("nonExistentId");
        assertThatThrownBy(() -> repository.getById(nonExistentId))
                .isInstanceOf(StorageSolutionNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }
}