package dev.codesoapbox.backity.core.gamefiledetails.domain;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.game.domain.Game;
import lombok.NonNull;

public record SourceFileDetails(
        @NonNull FileSourceId sourceId,
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
