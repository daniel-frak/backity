package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * A version of a game file, either not yet backed up, already backed up or anything in-between.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameFile {

    @NonNull
    @EqualsAndHashCode.Include
    private GameFileId id;

    @NonNull
    private GameId gameId;

    @NonNull
    private GameProviderId gameProviderId;

    @NonNull
    private String originalGameTitle;

    @NonNull
    private String fileTitle;

    @NonNull
    private String version;

    @NonNull
    private String url;

    @NonNull
    private String originalFileName;

    @NonNull
    private FileSize size;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public static GameFile createFor(Game game, DiscoveredFile discoveredFile) {
        return new GameFile(
                GameFileId.newInstance(),
                game.getId(),
                discoveredFile.gameProviderId(),
                discoveredFile.originalGameTitle(),
                discoveredFile.fileTitle(),
                discoveredFile.version(),
                discoveredFile.url(),
                discoveredFile.originalFileName(),
                discoveredFile.size(),
                null,
                null
        );
    }
}
