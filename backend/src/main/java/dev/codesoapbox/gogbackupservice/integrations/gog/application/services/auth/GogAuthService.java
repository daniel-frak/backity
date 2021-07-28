package dev.codesoapbox.gogbackupservice.integrations.gog.application.services.auth;

import dev.codesoapbox.gogbackupservice.integrations.gog.application.dto.auth.remote.GogAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GogAuthService {

    private String accessToken;
    private String refreshToken;
    private Integer expiresInSeconds;
    private LocalDateTime expirationTime;

    private final GogAuthClient gogAuthClient;

    public boolean isAuthenticated() {
        return accessToken != null
                && (expirationTime.isEqual(LocalDateTime.now()) || expirationTime.isAfter(LocalDateTime.now()));
    }

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

    public void refresh() {
        refresh(refreshToken);
    }

    public void refresh(String refreshToken) {
        GogAuthenticationResponse response = gogAuthClient.refreshToken(refreshToken);
        updateVariables(response);
    }

    public String getAccessToken() {
        if(accessToken == null) {
            throw new RuntimeException("You must authenticate before using the GOG API!");
        }
        if(expirationTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Access token expired!");
        }

        return accessToken;
    }

    public String getRefreshToken() {
        if(refreshToken == null) {
            throw new RuntimeException("You must authenticate before using the GOG API!");
        }

        return refreshToken;
    }

    // Check every minute
    @Scheduled(fixedRate = 60000)
    public void checkIfAccessTokenNeedsRefreshing() {
        log.debug("Checking access token expiration...");

        if(accessToken == null) {
            log.debug("No access token to expire");
            return;
        }

        // Refresh token when it's halfway to expiring
        if(expirationTime.minusSeconds(expiresInSeconds / 2).isBefore(LocalDateTime.now())) {
            log.info("Refreshing access token...");
            refresh();
        }
    }
}
