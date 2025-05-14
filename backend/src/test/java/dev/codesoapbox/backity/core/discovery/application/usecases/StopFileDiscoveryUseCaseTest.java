package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StopFileDiscoveryUseCaseTest {

    private StopFileDiscoveryUseCase useCase;

    @Mock
    private GameContentDiscoveryService gameContentDiscoveryService;

    @BeforeEach
    void setUp() {
        useCase = new StopFileDiscoveryUseCase(gameContentDiscoveryService);
    }

    @Test
    void shouldStopFileDiscovery() {
        useCase.stopFileDiscovery();

        verify(gameContentDiscoveryService).stopContentDiscovery();
    }
}