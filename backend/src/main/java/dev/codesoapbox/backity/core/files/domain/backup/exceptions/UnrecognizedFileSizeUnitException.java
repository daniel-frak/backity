package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

public class UnrecognizedFileSizeUnitException extends RuntimeException {

    public UnrecognizedFileSizeUnitException(String unit) {
        super("File size unit unrecognized: " + unit);
    }
}
