package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "FileBackupStartedMessage")
public record FileBackupStartedWsMessage(
        String gameFileDetailsId,
        String originalGameTitle,
        String fileTitle,
        String version,
        String originalFileName,
        String size,
        String filePath
) {
}
