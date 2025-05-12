package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogOutOfGogUseCase {

    private final GogAuthService authService;

    public void logOutOfGog() {
        authService.logOut();
    }
}
