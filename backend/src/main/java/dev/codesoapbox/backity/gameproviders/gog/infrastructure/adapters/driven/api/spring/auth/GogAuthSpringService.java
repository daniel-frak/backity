package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.exceptions.GogAuthException;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.model.remote.GogAuthenticationResponse;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class GogAuthSpringService implements GogAuthService {

    private final Clock clock;

    private String accessToken;
    private String refreshToken;
    private Integer expiresInSeconds;
    private LocalDateTime expirationTime;

    private final GogAuthClient gogAuthClient;

    @Override
    public boolean isAuthenticated() {
        return accessToken != null && expirationTime.isAfter(LocalDateTime.now(clock));
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
        this.expirationTime = LocalDateTime.now(clock).plusSeconds(response.getExpiresIn());
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
        validateIsAuthenticated();
        validateNotExpired();

        return accessToken;
    }

    private void validateIsAuthenticated() {
        if (accessToken == null) {
            throw new GogAuthException("You must authenticate before using the GOG API!");
        }
    }

    private void validateNotExpired() {
        if (hasExpired(expirationTime)) {
            throw new GogAuthException("Access token expired!");
        }
    }

    private boolean hasExpired(LocalDateTime expirationTime) {
        return !expirationTime.isAfter(LocalDateTime.now(clock));
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

        if (refreshTokenIsAtLeastHalfwayToExpiring()) {
            log.info("Refreshing access token...");
            refresh();
        }
    }

    private boolean refreshTokenIsAtLeastHalfwayToExpiring() {
        LocalDateTime halfwayToActualExpiration = expirationTime.minusSeconds(expiresInSeconds / 2);
        return hasExpired(halfwayToActualExpiration);
    }

    @Override
    public void logOut() {
        accessToken = null;
        refreshToken = null;
        expiresInSeconds = null;
        expirationTime = null;
    }
}
