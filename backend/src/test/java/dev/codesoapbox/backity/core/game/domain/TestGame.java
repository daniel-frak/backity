package dev.codesoapbox.backity.core.game.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(builderClassName = "Builder", builderMethodName = "anyBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public final class TestGame {

    @lombok.Builder.Default
    private GameId id = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");

    @lombok.Builder.Default
    private String title = "Test Game";

    @lombok.Builder.Default
    private LocalDateTime dateCreated = LocalDateTime.parse("2022-04-29T14:15:53");

    @lombok.Builder.Default
    private LocalDateTime dateModified = LocalDateTime.parse("2023-04-29T14:15:53");

    public static Game any() {
        return anyBuilder().build();
    }

    public static TestGame.Builder anyBuilder() {
        return new Builder();
    }

    public static class Builder {

        public Game build() {
            TestGame temp = internalBuilder();
            return new Game(temp.id, temp.dateCreated, temp.dateModified, temp.title);
        }
    }
}
