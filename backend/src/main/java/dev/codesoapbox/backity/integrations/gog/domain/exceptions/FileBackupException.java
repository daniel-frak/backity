package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

public class FileBackupException extends RuntimeException {

    public FileBackupException(String message) {
        super(message);
    }

    public FileBackupException(String message, Throwable cause) {
        super(message, cause);
    }
}