package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

public class GameBackupRequestFailedException extends RuntimeException {

    public GameBackupRequestFailedException(String fileUrl, Throwable cause) {
        super("An error occurred while backing up file: " + fileUrl, cause);
    }

    public GameBackupRequestFailedException(String fileUrl, String message) {
        super("An error occurred while backing up file: " + fileUrl + ". " + message);
    }
}
