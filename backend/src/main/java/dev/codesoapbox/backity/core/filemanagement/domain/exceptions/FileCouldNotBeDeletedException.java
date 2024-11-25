package dev.codesoapbox.backity.core.filemanagement.domain.exceptions;

public class FileCouldNotBeDeletedException extends RuntimeException {

    public FileCouldNotBeDeletedException(String path, Throwable cause) {
        super("File could not be deleted: " + path, cause);
    }
}
