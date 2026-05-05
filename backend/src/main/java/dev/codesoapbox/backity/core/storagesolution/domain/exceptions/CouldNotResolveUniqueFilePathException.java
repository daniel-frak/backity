package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.FileName;

public class CouldNotResolveUniqueFilePathException extends RuntimeException {

    public CouldNotResolveUniqueFilePathException(
            GameTitle gameTitle, FileName fileName, int attemptNumber) {
        super("Could not resolve unique file path for game '" + gameTitle + "' and file '" + fileName
              + "' after " + attemptNumber + " attempts");
    }
}
