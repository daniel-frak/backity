package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;

public class FileCouldNotBeDeletedException extends RuntimeException {

    public FileCouldNotBeDeletedException(FilePath filePath, Throwable cause) {
        super("File could not be deleted: " + filePath, cause);
    }
}
