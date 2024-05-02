package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.RefreshTokenResponseHttpDto;
import dev.codesoapbox.backity.integrations.gog.application.RefreshGogAccessTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@GogAuthRestResource
@RequiredArgsConstructor
public class RefreshGogAccessTokenController {

    private final RefreshGogAccessTokenUseCase useCase;

    @Operation(summary = "Refresh access token", description = "Refreshes the access token using a refresh token")
    @GetMapping("refresh")
    public RefreshTokenResponseHttpDto refreshAccessToken(@RequestParam("refresh_token") String refreshToken) {
        String accessToken = useCase.refreshAccessToken(refreshToken);
        return RefreshTokenResponseHttpDto.of(accessToken);
    }
}
