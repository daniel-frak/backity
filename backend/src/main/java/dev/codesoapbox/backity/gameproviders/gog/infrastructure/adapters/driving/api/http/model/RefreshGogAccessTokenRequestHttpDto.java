package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "RefreshGogAccessTokenRequest")
public record RefreshGogAccessTokenRequestHttpDto(
        @NotBlank
        String refreshToken
) {
}
