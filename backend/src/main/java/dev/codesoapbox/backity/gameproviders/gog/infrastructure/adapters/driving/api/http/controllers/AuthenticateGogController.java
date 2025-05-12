package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.RefreshTokenHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.AuthenticateGogUseCase;
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
    public RefreshTokenHttpDto authenticate(@RequestParam("code") String code) {
        String refreshToken = useCase.authenticateAndGetRefreshToken(code);
        return RefreshTokenHttpDto.of(refreshToken);
    }
}
