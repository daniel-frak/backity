package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Progress")
public record ProgressHttpDto(
        @NotNull int percentage,
        @NotNull long timeLeftSeconds
) {
}
