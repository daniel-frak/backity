package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "BackupTarget")
public record BackupTargetHttpDto(
        @NotNull String id,
        @NotNull String name,
        @NotNull String storageSolutionId,
        @NotNull String pathTemplate
) {
}
