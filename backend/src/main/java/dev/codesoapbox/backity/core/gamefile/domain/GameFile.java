package dev.codesoapbox.backity.core.gamefile.domain;

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
    private FileSource fileSource;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public static GameFile createFor(Game game, FileSource fileSource) {
        return new GameFile(
                GameFileId.newInstance(),
                game.getId(),
                fileSource,
                null,
                null
        );
    }
}
