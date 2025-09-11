package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.RefreshGogAccessTokenUseCase;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.RefreshGogAccessTokenRequestHttpDto;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model.RefreshTokenHttpDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@GogAuthRestResource
@RequiredArgsConstructor
public class RefreshGogAccessTokenController {

    private final RefreshGogAccessTokenUseCase useCase;

    @Operation(summary = "Refresh access token", description = "Refreshes the access token using a refresh token")
    @PutMapping
    public RefreshTokenHttpDto refreshGogAccessToken(
            @RequestBody @Valid RefreshGogAccessTokenRequestHttpDto refreshGogAccessTokenRequestHttpDto) {
        String accessToken = useCase.refreshAccessToken(refreshGogAccessTokenRequestHttpDto.refreshToken());
        return RefreshTokenHttpDto.of(accessToken);
    }
}
