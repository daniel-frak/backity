package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "FileDetails")
public record FileDetailsHttpDto(
        String id,
        String gameId,
        SourceFileDetailsHttpDto sourceFileDetails,
        BackupDetailsHttpDto backupDetails,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
