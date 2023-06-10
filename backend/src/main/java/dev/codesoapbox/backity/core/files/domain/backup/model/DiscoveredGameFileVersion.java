package dev.codesoapbox.backity.core.files.domain.backup.model;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import lombok.NonNull;

public record DiscoveredGameFileVersion(
        @NonNull String source,
        @NonNull String gameTitle,
        @NonNull String title,
        @NonNull String version,
        @NonNull String url,
        @NonNull String originalFileName,
        @NonNull String size
) {
    public GameFileVersion associateWith(Game game) {
        return new GameFileVersion(
                null,
                source,
                url,
                title,
                originalFileName,
                null,
                gameTitle,
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
