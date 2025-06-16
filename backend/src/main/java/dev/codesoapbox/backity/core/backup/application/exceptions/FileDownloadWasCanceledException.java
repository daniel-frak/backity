package dev.codesoapbox.backity.core.backup.application.exceptions;

public class FileDownloadWasCanceledException extends RuntimeException {

    public FileDownloadWasCanceledException(String filePath) {
        super("File download was canceled for '" + filePath + "'");
    }
}
