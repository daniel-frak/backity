package dev.codesoapbox.backity.integrations.gog.application.usecases;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticateGogUseCase {

    private final GogAuthService authService;

    public String authenticateAndGetRefreshToken(String code) {
        authService.authenticate(code);
        return authService.getRefreshToken();
    }
}
