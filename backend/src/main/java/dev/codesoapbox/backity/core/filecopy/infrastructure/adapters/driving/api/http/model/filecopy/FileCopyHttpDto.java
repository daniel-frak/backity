package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileCopyStatusHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "FileCopy")
public record FileCopyHttpDto(
        @NotNull String id,
        @NotNull FileCopyNaturalIdHttpDto naturalId,
        @NotNull FileCopyStatusHttpDto status,
        String failedReason,
        String filePath,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
