package dev.codesoapbox.backity.integrations.gog.application.usecases;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogOutOfGogUseCase {

    private final GogAuthService authService;

    public void logOutOfGog() {
        authService.logOut();
    }
}
