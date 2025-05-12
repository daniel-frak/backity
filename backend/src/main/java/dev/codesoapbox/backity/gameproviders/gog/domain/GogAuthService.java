package dev.codesoapbox.backity.gameproviders.gog.domain;

public interface GogAuthService {

    boolean isAuthenticated();

    void authenticate(String code);

    void refresh();

    void refresh(String refreshToken);

    String getAccessToken();

    String getRefreshToken();

    void refreshAccessTokenIfNeeded();

    void logOut();
}
