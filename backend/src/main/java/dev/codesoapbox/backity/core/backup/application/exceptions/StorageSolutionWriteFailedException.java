package dev.codesoapbox.backity.core.backup.application.exceptions;

public class StorageSolutionWriteFailedException extends RuntimeException {

    public StorageSolutionWriteFailedException(String message) {
        super(message);
    }

    public StorageSolutionWriteFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}