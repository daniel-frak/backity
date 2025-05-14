package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartFileDiscoveryUseCaseTest {

    private StartFileDiscoveryUseCase useCase;

    @Mock
    private GameContentDiscoveryService gameContentDiscoveryService;

    @BeforeEach
    void setUp() {
        useCase = new StartFileDiscoveryUseCase(gameContentDiscoveryService);
    }

    @Test
    void shouldStartFileDiscovery() {
        useCase.startFileDiscovery();

        verify(gameContentDiscoveryService).startContentDiscovery();
    }
}