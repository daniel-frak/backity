package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions;

public class FileDownloadException extends RuntimeException {

    public FileDownloadException(String message) {
        super(message);
    }

    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}