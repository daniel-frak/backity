package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticateGogUseCase {

    private final GogAuthService authService;

    public String authenticateAndGetRefreshToken(String code) {
        authService.authenticate(code);
        return authService.getRefreshToken();
    }
}
