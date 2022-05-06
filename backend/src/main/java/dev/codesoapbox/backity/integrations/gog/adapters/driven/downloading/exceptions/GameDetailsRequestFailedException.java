package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions;

public class GameDetailsRequestFailedException extends RuntimeException {

    public GameDetailsRequestFailedException(String gameId, Throwable cause) {
        super("Could not retrieve game details for game id: " + gameId, cause);
    }
}
