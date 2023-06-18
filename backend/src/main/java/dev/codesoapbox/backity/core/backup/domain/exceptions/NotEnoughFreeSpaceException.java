package dev.codesoapbox.backity.core.backup.domain.exceptions;

public class NotEnoughFreeSpaceException extends RuntimeException {

    public NotEnoughFreeSpaceException(String filePath) {
        super("Not enough space left to save: " + filePath);
    }
}
