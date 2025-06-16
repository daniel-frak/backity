package dev.codesoapbox.backity.core.backup.application.exceptions;

public class ConcurrentFileDownloadException extends RuntimeException {

    public ConcurrentFileDownloadException(String filePath) {
        super("File '" + filePath + "' is currently being downloaded by another thread");
    }
}
