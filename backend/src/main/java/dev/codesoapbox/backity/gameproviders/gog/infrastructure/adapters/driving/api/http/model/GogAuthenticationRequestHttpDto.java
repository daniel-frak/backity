package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "GogAuthenticationRequest")
public record GogAuthenticationRequestHttpDto(
        @NotBlank
        String code
) {
}
