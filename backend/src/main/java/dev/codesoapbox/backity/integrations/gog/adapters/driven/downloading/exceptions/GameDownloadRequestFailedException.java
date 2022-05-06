package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions;

public class GameDownloadRequestFailedException extends RuntimeException {

    public GameDownloadRequestFailedException(String gameFileUrl, Throwable cause) {
        super("An error occurred while downloading game file: " + gameFileUrl, cause);
    }
}
