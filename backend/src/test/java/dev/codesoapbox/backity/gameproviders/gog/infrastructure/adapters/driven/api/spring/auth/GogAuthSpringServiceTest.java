package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.exceptions.GogAuthException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.model.remote.GogAuthenticationResponse;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.model.remote.TestGogAuthenticationResponse;
import dev.codesoapbox.backity.testing.time.FakeClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogAuthSpringServiceTest {

    private GogAuthSpringService gogAuthService;

    @Mock
    private GogAuthClient gogAuthClient;

    private FakeClock clock;

    @BeforeEach
    void setUp() {
        clock = FakeClock.atEpochUtc();
        gogAuthService = new GogAuthSpringService(clock, gogAuthClient);
    }

    private void authenticateCorrectly(GogAuthenticationResponse gogAuthResponse) {
        var code = "someCode";

        when(gogAuthClient.getInitialToken(code))
                .thenReturn(gogAuthResponse);

        gogAuthService.authenticate(code);
    }

    private void tokenCanBeRefreshed(String oldRefreshToken, GogAuthenticationResponse gogAuthResponseRefreshed) {
        when(gogAuthClient.refreshToken(oldRefreshToken))
                .thenReturn(gogAuthResponseRefreshed);
    }

    @Nested
    class Authentication {

        @Test
        void shouldAuthenticate() {
            authenticateCorrectly(TestGogAuthenticationResponse.valid());

            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }

        @Test
        void shouldReturnFalseGivenAccessTokenMissing() {
            assertThat(gogAuthService.isAuthenticated()).isFalse();
        }

        @Test
        void shouldReturnFalseGivenAuthenticationExpired() {
            GogAuthenticationResponse gogAuthResponse = TestGogAuthenticationResponse.valid();
            authenticateCorrectly(gogAuthResponse);
            clock.moveForward(Duration.ofSeconds(gogAuthResponse.getExpiresIn()));

            assertThat(gogAuthService.isAuthenticated()).isFalse();
        }
    }

    @Nested
    class Refresh {

        @Test
        void shouldRefreshAuthentication() {
            GogAuthenticationResponse gogAuthResponse = TestGogAuthenticationResponse.valid();
            authenticateCorrectly(gogAuthResponse);
            clock.moveForward(Duration.ofSeconds(gogAuthResponse.getExpiresIn()));
            tokenCanBeRefreshed(gogAuthResponse.getRefreshToken(), gogAuthResponse);

            gogAuthService.refresh();

            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }
    }

    @Nested
    class GetAccessToken {

        @Test
        void shouldReturnAccessTokenGivenAuthenticatedAndNotExpired() {
            GogAuthenticationResponse gogAuthResponse = TestGogAuthenticationResponse.valid();
            authenticateCorrectly(gogAuthResponse);

            assertThat(gogAuthService.getAccessToken()).isEqualTo(gogAuthResponse.getAccessToken());
        }

        @Test
        void shouldThrowGivenNotAuthenticated() {
            assertThatThrownBy(() -> gogAuthService.getAccessToken())
                    .isInstanceOf(GogAuthException.class)
                    .hasMessageContaining("must authenticate");
        }

        @Test
        void shouldThrowGivenAccessTokenIsExpiredOnBoundary() {
            GogAuthenticationResponse gogAuthResponse = TestGogAuthenticationResponse.valid();
            authenticateCorrectly(gogAuthResponse);
            clock.moveForward(Duration.ofSeconds(gogAuthResponse.getExpiresIn()));

            assertThatThrownBy(() -> gogAuthService.getAccessToken())
                    .isInstanceOf(GogAuthException.class)
                    .hasMessageContaining("Access token expired");
        }

        @Test
        void shouldThrowGivenAccessTokenIsExpiredBeyondBoundary() {
            GogAuthenticationResponse gogAuthResponse = TestGogAuthenticationResponse.valid();
            authenticateCorrectly(gogAuthResponse);
            clock.moveForward(Duration.ofSeconds(gogAuthResponse.getExpiresIn() + 1));

            assertThatThrownBy(() -> gogAuthService.getAccessToken())
                    .isInstanceOf(GogAuthException.class)
                    .hasMessageContaining("Access token expired");
        }
    }

    @Nested
    class GetRefreshToken {

        @Test
        void shouldGetRefreshTokenGivenAuthenticated() {
            authenticateCorrectly(TestGogAuthenticationResponse.valid());

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
            GogAuthenticationResponse gogAuthResponse = TestGogAuthenticationResponse.valid();
            authenticateCorrectly(gogAuthResponse);

            gogAuthService.refreshAccessTokenIfNeeded();

            verifyNoMoreInteractions(gogAuthClient);
            assertThat(gogAuthService.isAuthenticated()).isTrue();
        }

        @Test
        void shouldRefreshAccessTokenGivenExactlyHalfwayToExpiry() {
            GogAuthenticationResponse gogAuthResponseInitial = TestGogAuthenticationResponse.validBuilder()
                    .withAccessToken("initialAccessToken")
                    .withExpiresIn(2) // Easily divisible by 2
                    .build();
            GogAuthenticationResponse gogAuthResponseRefreshed = TestGogAuthenticationResponse.validBuilder()
                    .withAccessToken("refreshedAccessToken")
                    .build();
            authenticateCorrectly(gogAuthResponseInitial);
            tokenCanBeRefreshed(gogAuthResponseInitial.getRefreshToken(), gogAuthResponseRefreshed);
            clock.moveForward(Duration.ofSeconds(gogAuthResponseInitial.getExpiresIn() / 2));

            gogAuthService.refreshAccessTokenIfNeeded();

            assertThat(gogAuthService.getAccessToken()).isEqualTo(gogAuthResponseRefreshed.getAccessToken());
        }
    }

    @Nested
    class LogOut {

        @Test
        void shouldLogOut() {
            authenticateCorrectly(TestGogAuthenticationResponse.valid());

            gogAuthService.logOut();

            assertThat(gogAuthService.isAuthenticated()).isFalse();
        }
    }
}