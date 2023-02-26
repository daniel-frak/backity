package dev.codesoapbox.backity.core.files.domain.downloading.exceptions;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;

public class GameFileDownloadUrlEmptyException extends IllegalArgumentException {

    public GameFileDownloadUrlEmptyException(Long id) {
        super("Game file url was null or empty for " + GameFileVersion.class.getSimpleName() + " with id: " + id);
    }
}
