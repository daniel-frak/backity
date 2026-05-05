package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.LogOutOfGogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@GogAuthRestResource
@RequiredArgsConstructor
public class LogOutOfGogController {

    private final LogOutOfGogUseCase useCase;

    @Operation(summary = "Log out of GOG", description = "Logs out of GOG")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logOutOfGog() {
        useCase.execute();
    }
}
