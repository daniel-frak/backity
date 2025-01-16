package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.application.LogOutOfGogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;

@GogAuthRestResource
@RequiredArgsConstructor
public class LogOutOfGogController {

    private final LogOutOfGogUseCase useCase;

    @Operation(summary = "Log out of GOG", description = "Logs out of GOG")
    @DeleteMapping
    public void logOutOfGog() {
        useCase.logOutOfGog();
    }
}
