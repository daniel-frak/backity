package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

public class GogAuthException extends RuntimeException {

    public GogAuthException(String message) {
        super(message);
    }
}