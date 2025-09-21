package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.exceptions;

public class GogAuthException extends RuntimeException {

    public GogAuthException(String message) {
        super(message);
    }
}