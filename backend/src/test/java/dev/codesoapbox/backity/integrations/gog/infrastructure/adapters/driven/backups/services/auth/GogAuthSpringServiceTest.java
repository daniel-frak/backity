package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driven.backups.services.auth;

import dev.codesoapbox.backity.integrations.gog.domain.exceptions.GogAuthException;
import dev.codesoapbox.backity.integrations.gog.domain.model.auth.remote.GogAuthenticationResponse;
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

    @Test
    void shouldAuthenticate() {
        authenticateCorrectly(3600);

        assertThat(gogAuthService.isAuthenticated()).isTrue();
    }

    private void authenticateCorrectly(int expiresInSeconds) {
        var code = "someCode";
        var gogAuthResponse = new GogAuthenticationResponse(
                "someAccessToken", "someRefreshToken", expiresInSeconds,
                "someSessionId", "someUserId");

        when(gogAuthClient.getInitialToken(code))
                .thenReturn(gogAuthResponse);

        gogAuthService.authenticate(code);
    }

    @Test
    void isAuthenticatedShouldReturnFalseIfAccessTokenMissing() {
        assertThat(gogAuthService.isAuthenticated()).isFalse();
    }

    @Test
    void isAuthenticatedShouldReturnFalseIfAuthenticationExpired() {
        authenticateCorrectly(-1);

        assertThat(gogAuthService.isAuthenticated()).isFalse();
    }

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

    @Test
    void shouldGetAccessToken() {
        authenticateCorrectly(3600);

        assertThat(gogAuthService.getAccessToken()).isEqualTo("someAccessToken");
    }

    @Test
    void getAccessTokenShouldThrowIfTokenIsNull() {
        assertThatThrownBy(() -> gogAuthService.getAccessToken())
                .isInstanceOf(GogAuthException.class)
                .hasMessageContaining("must authenticate");
    }

    @Test
    void getAccessTokenShouldThrowIfTokenIsExpired() {
        authenticateCorrectly(-1);
        assertThatThrownBy(() -> gogAuthService.getAccessToken())
                .isInstanceOf(GogAuthException.class)
                .hasMessageContaining("Access token expired");
    }

    @Test
    void shouldGetRefreshToken() {
        authenticateCorrectly(3600);

        assertThat(gogAuthService.getRefreshToken()).isEqualTo("someRefreshToken");
    }

    @Test
    void getRefreshTokenShouldThrowIfTokenIsNull() {
        assertThatThrownBy(() -> gogAuthService.getRefreshToken())
                .isInstanceOf(GogAuthException.class)
                .hasMessageContaining("must authenticate");
    }

    @Test
    void shouldNotRefreshAccessTokenIfNotAuthenticated() {
        gogAuthService.refreshAccessTokenIfNeeded();

        verifyNoInteractions(gogAuthClient);
        assertThat(gogAuthService.isAuthenticated()).isFalse();
    }

    @Test
    void shouldNotRefreshAccessTokenIfNotNeeded() {
        authenticateCorrectly(3600);

        gogAuthService.refreshAccessTokenIfNeeded();

        verifyNoMoreInteractions(gogAuthClient);
        assertThat(gogAuthService.isAuthenticated()).isTrue();
    }

    @Test
    void shouldRefreshAccessTokenIfNeeded() {
        authenticateCorrectly(-1);

        var gogAuthResponseRefreshed = new GogAuthenticationResponse("someAccessToken",
                "someRefreshToken", 3600, "someSessionId", "someUserId");

        when(gogAuthClient.refreshToken("someRefreshToken"))
                .thenReturn(gogAuthResponseRefreshed);

        gogAuthService.refreshAccessTokenIfNeeded();

        assertThat(gogAuthService.isAuthenticated()).isTrue();
    }

    @Test
    void shouldLogOut() {
        authenticateCorrectly(3600);

        gogAuthService.logOut();

        assertThat(gogAuthService.isAuthenticated()).isFalse();
    }
}