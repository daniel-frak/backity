package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@IncludeInDocumentation
@Schema(name = "FileBackupProgressUpdatedEvent")
public record FileBackupProgressUpdatedWsEvent(
        @NotNull int percentage,
        @NotNull long timeLeftSeconds
) {
}
