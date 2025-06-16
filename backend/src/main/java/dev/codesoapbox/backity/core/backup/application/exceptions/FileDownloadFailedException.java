package dev.codesoapbox.backity.core.backup.application.exceptions;

public class FileDownloadFailedException extends RuntimeException {

    public FileDownloadFailedException(String message) {
        super(message);
    }

    public FileDownloadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}