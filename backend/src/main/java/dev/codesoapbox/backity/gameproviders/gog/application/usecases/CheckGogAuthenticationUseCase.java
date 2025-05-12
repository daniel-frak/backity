package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CheckGogAuthenticationUseCase {

    private final GogAuthService authService;

    public boolean isAuthenticated() {
        return authService.isAuthenticated();
    }
}
