package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "FileDetails")
public record FileDetailsHttpDto(
        @NotNull String id,
        @NotNull String gameId,
        @NotNull SourceFileDetailsHttpDto sourceFileDetails,
        @NotNull BackupDetailsHttpDto backupDetails,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
