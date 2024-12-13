package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@IncludeInDocumentation
@Schema(name = "FileBackupStartedEvent")
public record FileBackupStartedWsEvent(
        @NotNull String gameFileId,
        @NotNull String originalGameTitle,
        @NotNull String fileTitle,
        @NotNull String version,
        @NotNull String originalFileName,
        @NotNull String size,
        String filePath
) {
}
