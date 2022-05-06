package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions;

public class GameListRequestFailedException extends RuntimeException {

    public GameListRequestFailedException(Throwable cause) {
        super("Could not retrieve game list", cause);
    }
}
