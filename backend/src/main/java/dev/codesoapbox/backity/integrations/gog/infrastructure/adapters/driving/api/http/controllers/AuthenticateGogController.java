package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.model.RefreshTokenResponseHttpDto;
import dev.codesoapbox.backity.integrations.gog.application.usecases.AuthenticateGogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@GogAuthRestResource
@RequiredArgsConstructor
public class AuthenticateGogController {

    private final AuthenticateGogUseCase useCase;

    @Operation(summary = "Authenticate", description = "Returns a refresh token based on a code")
    @PostMapping
    public RefreshTokenResponseHttpDto authenticate(@RequestParam("code") String code) {
        String refreshToken = useCase.authenticateAndGetRefreshToken(code);
        return RefreshTokenResponseHttpDto.of(refreshToken);
    }
}
