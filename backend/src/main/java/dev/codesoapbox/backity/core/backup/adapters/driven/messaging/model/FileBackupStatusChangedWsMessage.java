package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "FileBackupStatusChangedMessage")
public record FileBackupStatusChangedWsMessage(
        String fileDetailsId,
        String newStatus,
        String failedReason
) {
}
