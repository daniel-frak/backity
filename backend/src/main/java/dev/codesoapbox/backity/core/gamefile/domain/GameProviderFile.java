package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.Game;
import lombok.NonNull;

import java.util.ArrayList;

public record GameProviderFile(
        @NonNull GameProviderId gameProviderId,
        @NonNull String originalGameTitle,
        @NonNull String fileTitle,
        @NonNull String version,
        @NonNull String url,
        @NonNull String originalFileName,
        @NonNull String size
) {

    public GameFile associateWith(Game game) {
        return new GameFile(
                GameFileId.newInstance(),
                game.getId(),
                this,
                new FileBackup(FileBackupStatus.DISCOVERED, null, null),
                null,
                null,
                new ArrayList<>()
        );
    }
}
