package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFileDiscoveryStatusListUseCaseTest {

    private GetFileDiscoveryStatusListUseCase useCase;

    @Mock
    private FileDiscoveryService fileDiscoveryService;

    @BeforeEach
    void setUp() {
        useCase = new GetFileDiscoveryStatusListUseCase(fileDiscoveryService);
    }

    @Test
    void shouldGetStatusList() {
        List<FileDiscoveryStatus> statuses = mockFileDiscoveryStatuses();

        List<FileDiscoveryStatus> result = useCase.getStatusList();

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(statuses);
    }

    private List<FileDiscoveryStatus> mockFileDiscoveryStatuses() {
        List<FileDiscoveryStatus> statuses =
                List.of(new FileDiscoveryStatus("GOG", true));
        when(fileDiscoveryService.getStatuses())
                .thenReturn(statuses);

        return statuses;
    }
}