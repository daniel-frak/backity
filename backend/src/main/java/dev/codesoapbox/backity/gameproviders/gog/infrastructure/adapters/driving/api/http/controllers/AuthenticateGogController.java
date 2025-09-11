package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogAuthenticationRequestHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.RefreshTokenHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.AuthenticateGogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@GogAuthRestResource
@RequiredArgsConstructor
public class AuthenticateGogController {

    private final AuthenticateGogUseCase useCase;

    @Operation(summary = "Authenticate", description = "Returns a refresh token based on a code")
    @PostMapping
    public RefreshTokenHttpDto authenticateGog(
            @RequestBody @Valid GogAuthenticationRequestHttpDto gogAuthenticationRequestHttpDto) {
        String refreshToken = useCase.authenticateAndGetRefreshToken(gogAuthenticationRequestHttpDto.code());
        return RefreshTokenHttpDto.of(refreshToken);
    }
}
