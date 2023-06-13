package dev.codesoapbox.backity.core.files.domain.game;

import lombok.Builder;

import java.util.UUID;

@Builder(builderClassName = "Builder", builderMethodName = "aGame", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public final class TestGame {

    @lombok.Builder.Default
    private GameId id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));

    @lombok.Builder.Default
    private String title = "Test Game";

    public static class Builder {

        public Game build() {
            TestGame temp = internalBuilder();
            return new Game(temp.id, temp.title);
        }
    }
}
