package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@IncludeInDocumentation
@Schema(name = "FileBackupStatusChangedEvent")
public record FileBackupStatusChangedWsEvent(
        @NotNull String gameFileId,
        @NotNull String newStatus,
        String failedReason
) {
}
