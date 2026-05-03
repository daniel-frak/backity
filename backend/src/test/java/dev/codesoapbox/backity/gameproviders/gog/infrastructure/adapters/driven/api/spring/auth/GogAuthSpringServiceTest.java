package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.exceptions.GogAuthException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.model.remote.GogAuthenticationResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogAuthSpringServiceTest {

    @InjectMocks
    private GogAuthSpringService gogAuthService;

    @Mock
    private GogAuthClient gogAuthClient;

    private void authenticateCorrectly(int expiresInSeconds) {
        var code = "someCode";
        var gogAuthResponse = new GogAuthenticationResponse(
                "someAccessToken", "someRefreshToken", expiresInSeconds,
                "someSessionId", "someUserId");

        when(gogAuthClient.getInitialToken(code))
                .thenReturn(gogAuthResponse);

        gogAuthService.authenticate(code);
    }

    @Nested
    class Authentication {

        @Test
        void shouldAuthenticate() {
            authenticateCorrectly(3600);

            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }

        @Test
        void shouldReturnFalseGivenAccessTokenMissing() {
            assertThat(gogAuthService.isAuthenticated()).isFalse();
        }

        @Test
        void shouldReturnFalseGivenAuthenticationExpired() {
            authenticateCorrectly(-1);

            assertThat(gogAuthService.isAuthenticated()).isFalse();
        }
    }

    @Nested
    class Refresh {

        @Test
        void shouldRefresh() {
            var gogAuthResponseRefreshed = new GogAuthenticationResponse("someAccessToken",
                    "someRefreshToken", 3600, "someSessionId", "someUserId");

            when(gogAuthClient.refreshToken("someRefreshToken"))
                    .thenReturn(gogAuthResponseRefreshed);

            authenticateCorrectly(-1);

            gogAuthService.refresh();

            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }

        @Test
        void shouldRefreshWithNewRefreshToken() {
            var gogAuthResponseRefreshed = new GogAuthenticationResponse("someAccessToken",
                    "someRefreshToken", 3600, "someSessionId", "someUserId");

            when(gogAuthClient.refreshToken("someCustomRefreshToken"))
                    .thenReturn(gogAuthResponseRefreshed);

            authenticateCorrectly(-1);

            gogAuthService.refresh("someCustomRefreshToken");

            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }
    }

    @Nested
    class GetAccessToken {

        @Test
        void shouldGetAccessToken() {
            authenticateCorrectly(3600);

            assertThat(gogAuthService.getAccessToken()).isEqualTo("someAccessToken");
        }

        @Test
        void shouldThrowGivenTokenIsNull() {
            assertThatThrownBy(() -> gogAuthService.getAccessToken())
                    .isInstanceOf(GogAuthException.class)
                    .hasMessageContaining("must authenticate");
        }

        @Test
        void shouldThrowGivenTokenIsExpired() {
            authenticateCorrectly(-1);
            assertThatThrownBy(() -> gogAuthService.getAccessToken())
                    .isInstanceOf(GogAuthException.class)
                    .hasMessageContaining("Access token expired");
        }
    }

    @Nested
    class GetRefreshToken {

        @Test
        void shouldGetRefreshToken() {
            authenticateCorrectly(3600);

            assertThat(gogAuthService.getRefreshToken()).isEqualTo("someRefreshToken");
        }

        @Test
        void shouldThrowGivenTokenIsNull() {
            assertThatThrownBy(() -> gogAuthService.getRefreshToken())
                    .isInstanceOf(GogAuthException.class)
                    .hasMessageContaining("must authenticate");
        }
    }

    @Nested
    class RefreshAccessTokenIfNeeded {

        @Test
        void shouldNotRefreshAccessTokenGivenNotAuthenticated() {
            gogAuthService.refreshAccessTokenIfNeeded();

            verifyNoInteractions(gogAuthClient);
            assertThat(gogAuthService.isAuthenticated()).isFalse();
        }

        @Test
        void shouldNotRefreshAccessTokenGivenNotNeeded() {
            authenticateCorrectly(3600);

            gogAuthService.refreshAccessTokenIfNeeded();

            verifyNoMoreInteractions(gogAuthClient);
            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }

        @Test
        void shouldRefreshAccessTokenGivenNeeded() {
            authenticateCorrectly(-1);

            var gogAuthResponseRefreshed = new GogAuthenticationResponse("someAccessToken",
                    "someRefreshToken", 3600, "someSessionId", "someUserId");

            when(gogAuthClient.refreshToken("someRefreshToken"))
                    .thenReturn(gogAuthResponseRefreshed);

            gogAuthService.refreshAccessTokenIfNeeded();

            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }
    }

    @Nested
    class LogOut {

        @Test
        void shouldLogOut() {
            authenticateCorrectly(3600);

            gogAuthService.logOut();

            assertThat(gogAuthService.isAuthenticated()).isFalse();
        }
    }
}