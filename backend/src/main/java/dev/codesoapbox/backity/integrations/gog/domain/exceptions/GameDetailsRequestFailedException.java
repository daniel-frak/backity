package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

public class GameDetailsRequestFailedException extends RuntimeException {

    public GameDetailsRequestFailedException(String gameId, Throwable cause) {
        super("Could not retrieve game details for game id: " + gameId, cause);
    }

    public GameDetailsRequestFailedException(String gameId, String message) {
        super("Could not retrieve game details for game id: " + gameId + ". " + message);
    }
}
