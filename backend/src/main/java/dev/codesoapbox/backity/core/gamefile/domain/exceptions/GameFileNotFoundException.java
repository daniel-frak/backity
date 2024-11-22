package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;

public class GameFileNotFoundException extends RuntimeException {

    public GameFileNotFoundException(GameFileId id) {
        super("Could not find " + GameFile.class.getSimpleName() + " with id=" + id);
    }
}
