package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryService;
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
    private FileDiscoveryService fileDiscoveryService;

    @BeforeEach
    void setUp() {
        useCase = new StartFileDiscoveryUseCase(fileDiscoveryService);
    }

    @Test
    void shouldStartFileDiscovery() {
        useCase.startFileDiscovery();

        verify(fileDiscoveryService).startFileDiscovery();
    }
}