package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyNaturalIdHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(name = "EnqueueFileCopyRequest")
public record EnqueueFileCopyRequestHttpDto(
        @NotNull @Valid FileCopyNaturalIdHttpDto fileCopyNaturalId
) {
}
