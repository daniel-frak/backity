package dev.codesoapbox.backity.integrations.gog.application;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckGogAuthenticationUseCaseTest {

    private CheckGogAuthenticationUseCase useCase;

    @Mock
    private GogAuthService authService;

    @BeforeEach
    void setUp() {
        useCase = new CheckGogAuthenticationUseCase(authService);
    }

    @Test
    void isAuthenticatedShouldReturnTrueGivenIsAuthenticated() {
        mockIsAuthenticated(true);

        boolean result = useCase.isAuthenticated();

        assertThat(result).isTrue();
    }

    private void mockIsAuthenticated(boolean isAuthenticated) {
        when(authService.isAuthenticated())
                .thenReturn(isAuthenticated);
    }

    @Test
    void isAuthenticatedShouldReturnFalseGivenIsNotAuthenticated() {
        mockIsAuthenticated(false);

        boolean result = useCase.isAuthenticated();

        assertThat(result).isFalse();
    }
}