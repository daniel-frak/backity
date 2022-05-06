package dev.codesoapbox.backity.core.files.downloading.domain.exceptions;

public class NotEnoughFreeSpaceException extends RuntimeException {

    public NotEnoughFreeSpaceException(String filePath) {
        super("Not enough space left to save: " + filePath);
    }
}
