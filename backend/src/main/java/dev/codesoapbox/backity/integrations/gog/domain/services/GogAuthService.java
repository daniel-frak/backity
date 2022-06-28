package dev.codesoapbox.backity.integrations.gog.domain.services;

public interface GogAuthService {
    boolean isAuthenticated();

    void authenticate(String code);

    void refresh();

    void refresh(String refreshToken);

    String getAccessToken();

    String getRefreshToken();

    void refreshAccessTokenIfNeeded();
}
