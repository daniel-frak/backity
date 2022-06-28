package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

public class GameDownloadRequestFailedException extends RuntimeException {

    public GameDownloadRequestFailedException(String gameFileUrl, Throwable cause) {
        super("An error occurred while downloading game file: " + gameFileUrl, cause);
    }

    public GameDownloadRequestFailedException(String gameFileUrl, String message) {
        super("An error occurred while downloading game file: " + gameFileUrl + ". " + message);
    }
}
