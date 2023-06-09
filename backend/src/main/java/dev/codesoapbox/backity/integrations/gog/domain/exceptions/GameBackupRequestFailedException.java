package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

public class GameBackupRequestFailedException extends RuntimeException {

    public GameBackupRequestFailedException(String gameFileUrl, Throwable cause) {
        super("An error occurred while backing up game file: " + gameFileUrl, cause);
    }

    public GameBackupRequestFailedException(String gameFileUrl, String message) {
        super("An error occurred while backing up game file: " + gameFileUrl + ". " + message);
    }
}
