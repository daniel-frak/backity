package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "FileBackupProgressUpdateMessage")
public record FileBackupProgressUpdateWsMessage(
        int percentage,
        long timeLeftSeconds
) {
}
