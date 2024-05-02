package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.application.CheckGogAuthenticationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@GogAuthRestResource
@RequiredArgsConstructor
public class CheckGogAuthenticationController {

    private final CheckGogAuthenticationUseCase useCase;

    @Operation(summary = "Check authentication",
            description = "Returns whether or not the user is currently authenticated")
    @GetMapping("/check")
    public boolean checkAuthentication() {
        return useCase.isAuthenticated();
    }
}
