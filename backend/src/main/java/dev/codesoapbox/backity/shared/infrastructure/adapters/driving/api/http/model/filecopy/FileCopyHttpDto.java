package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.FileBackupStatusHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "FileCopy")
public record FileCopyHttpDto(
        @NotNull String id,
        @NotNull String gameFileId,
        @NotNull String backupTargetId,
        @NotNull FileBackupStatusHttpDto status,
        String failedReason,
        String filePath,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
