package dev.codesoapbox.backity.core.files.downloading.domain.exceptions;

public class UnrecognizedFileSizeUnitException extends RuntimeException {

    public UnrecognizedFileSizeUnitException(String unit) {
        super("File size unit unrecognized: " + unit);
    }
}
