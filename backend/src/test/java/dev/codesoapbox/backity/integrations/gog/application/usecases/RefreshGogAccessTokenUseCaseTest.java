package dev.codesoapbox.backity.integrations.gog.application.usecases;

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
class RefreshGogAccessTokenUseCaseTest {

    private RefreshGogAccessTokenUseCase useCase;

    @Mock
    private GogAuthService authService;

    @BeforeEach
    void setUp() {
        useCase = new RefreshGogAccessTokenUseCase(authService);
    }

    @Test
    void shouldRefreshAccessToken() {
        String oldRefreshToken = "oldRefreshToken";
        String newRefreshToken = mockNewRefreshToken();

        String result = useCase.refreshAccessToken(oldRefreshToken);

        assertThat(result)
                .isEqualTo(newRefreshToken);
        InOrder inOrder = inOrder(authService);
        inOrder.verify(authService).refresh(oldRefreshToken);
        inOrder.verify(authService).getRefreshToken();
    }

    private String mockNewRefreshToken() {
        String newRefreshToken = "newRefreshToken";
        when(authService.getRefreshToken())
                .thenReturn(newRefreshToken);

        return newRefreshToken;
    }
}