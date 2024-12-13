package dev.codesoapbox.backity.shared.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "GameFile")
public record GameFileHttpDto(
        @NotNull String id,
        @NotNull String gameId,
        @NotNull GameProviderFileHttpDto gameProviderFile,
        @NotNull FileBackupHttpDto fileBackup,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
