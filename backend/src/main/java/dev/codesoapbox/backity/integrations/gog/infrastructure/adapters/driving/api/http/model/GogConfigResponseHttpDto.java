package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.model;

import jakarta.validation.constraints.NotNull;

public record GogConfigResponseHttpDto(
        @NotNull String userAuthUrl
) {
}
