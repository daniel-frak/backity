package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.application.CheckGogAuthenticationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GogAuthRestResource
@RequiredArgsConstructor
public class GetGogAuthenticationStatusController {

    private final CheckGogAuthenticationUseCase useCase;

    @Operation(summary = "Get authentication status",
            description = "Returns whether or not the user is currently authenticated")
    @GetMapping
    public boolean checkAuthentication() {
        return useCase.isAuthenticated();
    }
}
