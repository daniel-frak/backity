package dev.codesoapbox.backity.core.backup.application.exceptions;

public class ConcurrentFileWriteException extends RuntimeException {

    public ConcurrentFileWriteException(String filePath) {
        super("File '" + filePath + "' is currently being written to by another thread");
    }
}
