package dev.codesoapbox.backity.integrations.gog.application.usecases;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshGogAccessTokenUseCase {

    private final GogAuthService authService;

    public String refreshAccessToken(String refreshToken) {
        authService.refresh(refreshToken);
        return authService.getRefreshToken();
    }
}
