package dev.codesoapbox.backity.integrations.gog.application;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateGogUseCaseTest {

    private AuthenticateGogUseCase useCase;

    @Mock
    private GogAuthService authService;

    @BeforeEach
    void setUp() {
        useCase = new AuthenticateGogUseCase(authService);
    }

    @Test
    void shouldAuthenticateAndGetRefreshToken() {
        String code = "someCode";
        String refreshToken = mockRefreshToken();

        String result = useCase.authenticateAndGetRefreshToken(code);

        assertThat(result)
                .isEqualTo(refreshToken);
        InOrder inOrder = inOrder(authService);
        inOrder.verify(authService).authenticate(code);
        inOrder.verify(authService).getRefreshToken();
    }

    private String mockRefreshToken() {
        String refreshToken = "someRefreshToken";
        when(authService.getRefreshToken())
                .thenReturn(refreshToken);

        return refreshToken;
    }
}