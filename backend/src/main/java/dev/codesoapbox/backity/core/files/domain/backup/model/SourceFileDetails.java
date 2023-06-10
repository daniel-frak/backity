package dev.codesoapbox.backity.core.files.domain.backup.model;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import lombok.NonNull;

public record SourceFileDetails(
        @NonNull String sourceId,
        @NonNull String originalGameTitle,
        @NonNull String fileTitle,
        @NonNull String version,
        @NonNull String url,
        @NonNull String originalFileName,
        @NonNull String size
) {
    public GameFileVersion associateWith(Game game) {
        return new GameFileVersion(
                null,
                sourceId,
                url,
                fileTitle,
                originalFileName,
                null,
                originalGameTitle,
                game.getId().value().toString(),
                version,
                size,
                null,
                null,
                FileBackupStatus.DISCOVERED,
                null
        );
    }
}
