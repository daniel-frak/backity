package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshGogAccessTokenUseCase {

    private final GogAuthService authService;

    public String refreshAccessToken(String refreshToken) {
        authService.refresh(refreshToken);
        return authService.getRefreshToken();
    }
}
