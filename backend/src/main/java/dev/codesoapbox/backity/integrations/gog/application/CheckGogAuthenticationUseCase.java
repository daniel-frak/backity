package dev.codesoapbox.backity.integrations.gog.application;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CheckGogAuthenticationUseCase {

    private final GogAuthService authService;

    public boolean isAuthenticated() {
        return authService.isAuthenticated();
    }
}
