package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "FileBackupStartedMessage")
public record FileBackupStartedWsEvent(
        String fileDetailsId,
        String originalGameTitle,
        String fileTitle,
        String version,
        String originalFileName,
        String size,
        String filePath
) {
}
