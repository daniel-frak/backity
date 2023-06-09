package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

public class NotEnoughFreeSpaceException extends RuntimeException {

    public NotEnoughFreeSpaceException(String filePath) {
        super("Not enough space left to save: " + filePath);
    }
}
