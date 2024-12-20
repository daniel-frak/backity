package dev.codesoapbox.backity.core.game.domain;

import lombok.Builder;

@Builder(builderClassName = "Builder", builderMethodName = "anyBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public final class TestGame {

    @lombok.Builder.Default
    private GameId id = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");

    @lombok.Builder.Default
    private String title = "Test Game";

    public static Game any() {
        return anyBuilder().build();
    }

    public static TestGame.Builder anyBuilder() {
        return new Builder();
    }

    public static class Builder {

        public Game build() {
            TestGame temp = internalBuilder();
            return new Game(temp.id, temp.title);
        }
    }
}
