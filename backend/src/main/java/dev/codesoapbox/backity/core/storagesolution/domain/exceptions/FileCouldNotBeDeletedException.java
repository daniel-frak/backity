package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

public class FileCouldNotBeDeletedException extends RuntimeException {

    public FileCouldNotBeDeletedException(String path, Throwable cause) {
        super("File could not be deleted: " + path, cause);
    }
}
