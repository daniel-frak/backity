package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryService;
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
    private FileDiscoveryService fileDiscoveryService;

    @BeforeEach
    void setUp() {
        useCase = new StopFileDiscoveryUseCase(fileDiscoveryService);
    }

    @Test
    void shouldStopFileDiscovery() {
        useCase.stopFileDiscovery();

        verify(fileDiscoveryService).stopFileDiscovery();
    }
}