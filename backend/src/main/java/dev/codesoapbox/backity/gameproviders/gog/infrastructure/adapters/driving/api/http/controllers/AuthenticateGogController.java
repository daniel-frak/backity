package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.AuthenticateGogUseCase;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.GogAuthenticationRequestHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.RefreshTokenHttpDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@GogAuthRestResource
@RequiredArgsConstructor
@Slf4j
public class AuthenticateGogController {

    private final AuthenticateGogUseCase useCase;

    @Operation(summary = "Authenticate", description = "Returns a refresh token based on a code")
    @PostMapping
    public RefreshTokenHttpDto authenticateGog(
            @RequestBody @Valid GogAuthenticationRequestHttpDto gogAuthenticationRequestHttpDto) {
        log.debug("Received a request for GOG auth.");
        String refreshToken = useCase.execute(gogAuthenticationRequestHttpDto.code());
        return RefreshTokenHttpDto.of(refreshToken);
    }
}
