package dev.codesoapbox.backity.gameproviders.gog.domain.exceptions;

public class GameListRequestFailedException extends RuntimeException {

    public GameListRequestFailedException(Throwable cause) {
        super("Could not retrieve game list", cause);
    }
}
