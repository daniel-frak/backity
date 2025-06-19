package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.game.domain.GameId;

import java.time.LocalDateTime;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild", builderMethodName = "gogBuilder")
public final class TestGameFile {

    @lombok.Builder.Default
    private GameFileId id = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

    @lombok.Builder.Default
    private GameId gameId = new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281");

    @lombok.Builder.Default
    private FileSource fileSource = TestFileSource.minimalGog();

    @lombok.Builder.Default
    private LocalDateTime dateCreated = LocalDateTime.parse("2022-04-29T14:15:53");

    @lombok.Builder.Default
    private LocalDateTime dateModified = LocalDateTime.parse("2023-04-29T14:15:53");

    public static GameFile gog() {
        return gogBuilder().build();
    }

    public static Builder gogBuilder() {
        return new Builder();
    }

    public static class Builder {

        public GameFile build() {
            var temp = internalBuild();
            return new GameFile(
                    temp.id,
                    temp.gameId,
                    temp.fileSource,
                    temp.dateCreated,
                    temp.dateModified
            );
        }
    }
}
