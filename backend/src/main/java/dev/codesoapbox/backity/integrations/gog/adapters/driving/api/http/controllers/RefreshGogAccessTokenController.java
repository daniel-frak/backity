package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.RefreshTokenResponseHttpDto;
import dev.codesoapbox.backity.integrations.gog.application.usecases.RefreshGogAccessTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@GogAuthRestResource
@RequiredArgsConstructor
public class RefreshGogAccessTokenController {

    private final RefreshGogAccessTokenUseCase useCase;

    @Operation(summary = "Refresh access token", description = "Refreshes the access token using a refresh token")
    @PutMapping
    public RefreshTokenResponseHttpDto refreshAccessToken(@RequestParam("refresh_token") String refreshToken) {
        String accessToken = useCase.refreshAccessToken(refreshToken);
        return RefreshTokenResponseHttpDto.of(accessToken);
    }
}
