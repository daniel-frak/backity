package dev.codesoapbox.backity.core.gamefiledetails.domain.exceptions;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;

public class GameFileDetailsNotFoundException extends RuntimeException {

    public GameFileDetailsNotFoundException(GameFileDetailsId id) {
        super("Could not find " + GameFileDetails.class.getSimpleName() + " with id=" + id);
    }
}
