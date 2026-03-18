package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.exceptions;

public class GameListRequestFailedException extends RuntimeException {

    public GameListRequestFailedException(Throwable cause) {
        super("Could not retrieve game list", cause);
    }
}
