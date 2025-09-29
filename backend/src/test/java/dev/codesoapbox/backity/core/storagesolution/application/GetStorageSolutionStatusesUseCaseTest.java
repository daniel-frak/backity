package dev.codesoapbox.backity.core.storagesolution.application;

import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetStorageSolutionStatusesUseCaseTest {

    private GetStorageSolutionStatusesUseCase useCase;

    @Mock
    private StorageSolutionRepository storageSolutionRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetStorageSolutionStatusesUseCase(storageSolutionRepository);
    }

    @Test
    void shouldGetStorageSolutionStatuses() {
        when(storageSolutionRepository.findAll())
                .thenReturn(List.of(new FakeUnixStorageSolution()));

        Map<StorageSolutionId, StorageSolutionStatus> result = useCase.getStorageSolutionStatuses();

        Map<StorageSolutionId, StorageSolutionStatus> expectedResult = Map.of(
                FakeUnixStorageSolution.DEFAULT_ID, StorageSolutionStatus.CONNECTED
        );
        assertThat(result).isEqualTo(expectedResult);
    }
}