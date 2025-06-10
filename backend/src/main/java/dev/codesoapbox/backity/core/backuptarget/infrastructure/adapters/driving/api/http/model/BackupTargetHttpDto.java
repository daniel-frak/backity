package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "BackupTarget")
public record BackupTargetHttpDto(
        @NotBlank String id,
        @NotBlank String name,
        @NotBlank String storageSolutionId,
        @NotBlank String pathTemplate
) {
}
