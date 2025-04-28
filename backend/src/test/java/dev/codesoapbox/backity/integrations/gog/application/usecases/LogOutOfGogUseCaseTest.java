package dev.codesoapbox.backity.integrations.gog.application.usecases;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogOutOfGogUseCaseTest {

    @Mock
    private GogAuthService authService;

    private LogOutOfGogUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LogOutOfGogUseCase(authService);
    }

    @Test
    void shouldLogOutOfGog() {
        useCase.logOutOfGog();
        verify(authService).logOut();
    }
}