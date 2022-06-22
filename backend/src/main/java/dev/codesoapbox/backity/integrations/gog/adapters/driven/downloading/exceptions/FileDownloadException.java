package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions;

public class FileDownloadException extends RuntimeException {

    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}