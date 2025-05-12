package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.auth;

import dev.codesoapbox.backity.gameproviders.gog.domain.exceptions.GogAuthException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.auth.model.remote.GogAuthenticationResponse;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class GogAuthSpringService implements GogAuthService {

    private String accessToken;
    private String refreshToken;
    private Integer expiresInSeconds;
    private LocalDateTime expirationTime;

    private final GogAuthClient gogAuthClient;

    @Override
    public boolean isAuthenticated() {
        return accessToken != null && expirationTime.isAfter(LocalDateTime.now());
    }

    @Override
    public void authenticate(String code) {
        GogAuthenticationResponse response = gogAuthClient.getInitialToken(code);
        updateVariables(response);
    }

    private void updateVariables(GogAuthenticationResponse response) {
        this.accessToken = response.getAccessToken();
        this.refreshToken = response.getRefreshToken();
        this.expiresInSeconds = response.getExpiresIn();
        this.expirationTime = LocalDateTime.now().plusSeconds(response.getExpiresIn());
    }

    @Override
    public void refresh() {
        refresh(refreshToken);
    }

    @Override
    public void refresh(String refreshToken) {
        GogAuthenticationResponse response = gogAuthClient.refreshToken(refreshToken);
        updateVariables(response);
    }

    @Override
    public String getAccessToken() {
        if (accessToken == null) {
            throw new GogAuthException("You must authenticate before using the GOG API!");
        }
        if (expirationTime.isBefore(LocalDateTime.now())) {
            throw new GogAuthException("Access token expired!");
        }

        return accessToken;
    }

    @Override
    public String getRefreshToken() {
        if (refreshToken == null) {
            throw new GogAuthException("You must authenticate before using the GOG API!");
        }

        return refreshToken;
    }

    @Override
    public void refreshAccessTokenIfNeeded() {
        log.debug("Checking access token expiration...");

        if (accessToken == null) {
            log.debug("No access token to expire");
            return;
        }

        // Refresh token when it's halfway to expiring
        if (expirationTime.minusSeconds(expiresInSeconds / 2).isBefore(LocalDateTime.now())) {
            log.info("Refreshing access token...");
            refresh();
        }
    }

    @Override
    public void logOut() {
        accessToken = null;
        refreshToken = null;
        expiresInSeconds = null;
        expirationTime = null;
    }
}
