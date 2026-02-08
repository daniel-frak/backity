package dev.codesoapbox.backity.core.sourcefile.domain;

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
 * A file provided by a Game Provider.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SourceFile {

    @NonNull
    @EqualsAndHashCode.Include
    private SourceFileId id;

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

    public static SourceFile createFor(Game game, DiscoveredFile discoveredFile) {
        return new SourceFile(
                SourceFileId.newInstance(),
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
