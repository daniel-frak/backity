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

    public GameFileDetails associateWith(Game game) {
        return new GameFileDetails(
                GameFileDetailsId.newInstance(),
                game.getId(),
                this,
                new BackupDetails(FileBackupStatus.DISCOVERED, null, null),
                null,
                null
        );
    }
}
