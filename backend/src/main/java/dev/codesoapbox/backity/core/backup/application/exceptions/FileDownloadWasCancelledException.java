package dev.codesoapbox.backity.core.backup.application.exceptions;

public class FileDownloadWasCancelledException extends RuntimeException {

    public FileDownloadWasCancelledException(String filePath) {
        super("File download was cancelled for '" + filePath + "'");
    }
}
