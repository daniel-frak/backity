package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "GameFileDetails")
public record GameFileDetailsHttpDto(
        String id,
        String gameId,
        SourceFileDetailsHttpDto sourceFileDetails,
        BackupDetailsHttpDto backupDetails,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
