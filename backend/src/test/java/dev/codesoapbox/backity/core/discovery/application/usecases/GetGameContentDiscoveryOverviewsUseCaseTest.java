package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryOverview;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryProgressTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGameContentDiscoveryOverviewsUseCaseTest {

    private GetGameContentDiscoveryOverviewsUseCase useCase;

    @Mock
    private GameContentDiscoveryProgressTracker discoveryProgressTracker;

    @BeforeEach
    void setUp() {
        useCase = new GetGameContentDiscoveryOverviewsUseCase(discoveryProgressTracker);
    }

    @Test
    void shouldGetDiscoveryOverviews() {
        List<GameContentDiscoveryOverview> overviews = mockGameContentDiscoveryOverviews();

        List<GameContentDiscoveryOverview> result = useCase.getDiscoveryOverviews();

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(overviews);
    }

    private List<GameContentDiscoveryOverview> mockGameContentDiscoveryOverviews() {
        var gameProviderId = new GameProviderId("TestGameProviderId");
        List<GameContentDiscoveryOverview> overviews =
                List.of(new GameContentDiscoveryOverview(
                        gameProviderId, true, null, null));
        when(discoveryProgressTracker.getDiscoveryOverviews())
                .thenReturn(overviews);

        return overviews;
    }
}