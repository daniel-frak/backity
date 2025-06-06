package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGameContentDiscoveryStatusListUseCaseTest {

    private GetGameContentDiscoveryStatusListUseCase useCase;

    @Mock
    private GameContentDiscoveryService gameContentDiscoveryService;

    @BeforeEach
    void setUp() {
        useCase = new GetGameContentDiscoveryStatusListUseCase(gameContentDiscoveryService);
    }

    @Test
    void shouldGetStatusList() {
        List<GameContentDiscoveryStatus> statuses = mockGameContentDiscoveryStatuses();

        List<GameContentDiscoveryStatus> result = useCase.getStatusList();

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(statuses);
    }

    private List<GameContentDiscoveryStatus> mockGameContentDiscoveryStatuses() {
        var gameProviderId = new GameProviderId("TestGameProviderId");
        List<GameContentDiscoveryStatus> statuses =
                List.of(new GameContentDiscoveryStatus(gameProviderId, true, null));
        when(gameContentDiscoveryService.getStatuses())
                .thenReturn(statuses);

        return statuses;
    }
}