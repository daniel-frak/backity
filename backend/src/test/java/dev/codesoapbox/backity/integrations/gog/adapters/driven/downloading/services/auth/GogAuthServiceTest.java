package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth;

import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.auth.remote.GogAuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogAuthServiceTest {

    @InjectMocks
    private GogAuthService gogAuthService;

    @Mock
    private GogAuthClient gogAuthClient;

    @Test
    void shouldAuthenticate() {
        authenticateCorrectly(3600);

        assertTrue(gogAuthService.isAuthenticated());
    }

    private void authenticateCorrectly(int expiresInSeconds) {
        var code = "someCode";
        var gogAuthResponse = new GogAuthenticationResponse("someAccessToken", "someRefreshToken",
                expiresInSeconds, "someSessionId", "someUserId");

        when(gogAuthClient.getInitialToken(code))
                .thenReturn(gogAuthResponse);

        gogAuthService.authenticate(code);
    }

    @Test
    void isAuthenticatedShouldReturnFalseIfAccessTokenMissing() {
        assertFalse(gogAuthService.isAuthenticated());
    }

    @Test
    void isAuthenticatedShouldReturnFalseIfAuthenticationExpired() {
        authenticateCorrectly(-1);

        assertFalse(gogAuthService.isAuthenticated());
    }

    @Test
    void shouldRefresh() {
        var gogAuthResponseRefreshed = new GogAuthenticationResponse("someAccessToken",
                "someRefreshToken", 3600, "someSessionId", "someUserId");

        when(gogAuthClient.refreshToken("someRefreshToken"))
                .thenReturn(gogAuthResponseRefreshed);

        authenticateCorrectly(-1);

        gogAuthService.refresh();

        assertTrue(gogAuthService.isAuthenticated());
    }

    @Test
    void shouldRefreshWithNewRefreshToken() {
        var gogAuthResponseRefreshed = new GogAuthenticationResponse("someAccessToken",
                "someRefreshToken", 3600, "someSessionId", "someUserId");

        when(gogAuthClient.refreshToken("someCustomRefreshToken"))
                .thenReturn(gogAuthResponseRefreshed);

        authenticateCorrectly(-1);

        gogAuthService.refresh("someCustomRefreshToken");

        assertTrue(gogAuthService.isAuthenticated());
    }

    @Test
    void shouldGetAccessToken() {
        authenticateCorrectly(3600);

        assertEquals("someAccessToken", gogAuthService.getAccessToken());
    }

    @Test
    void shouldGetRefreshToken() {
        authenticateCorrectly(3600);

        assertEquals("someRefreshToken", gogAuthService.getRefreshToken());
    }

    @Test
    void shouldNotRefreshAccessTokenIfNotAuthenticated() {
        gogAuthService.refreshAccessTokenIfNeeded();

        verifyNoInteractions(gogAuthClient);
        assertFalse(gogAuthService.isAuthenticated());
    }

    @Test
    void shouldNotRefreshAccessTokenIfNotNeeded() {
        authenticateCorrectly(3600);

        gogAuthService.refreshAccessTokenIfNeeded();

        verifyNoMoreInteractions(gogAuthClient);
        assertTrue(gogAuthService.isAuthenticated());
    }

    @Test
    void shouldRefreshAccessTokenIfNeeded() {
        authenticateCorrectly(-1);

        var gogAuthResponseRefreshed = new GogAuthenticationResponse("someAccessToken",
                "someRefreshToken", 3600, "someSessionId", "someUserId");

        when(gogAuthClient.refreshToken("someRefreshToken"))
                .thenReturn(gogAuthResponseRefreshed);

        gogAuthService.refreshAccessTokenIfNeeded();

        assertTrue(gogAuthService.isAuthenticated());
    }
}