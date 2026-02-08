package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.game.domain.GameId;

import java.time.LocalDateTime;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild", builderMethodName = "gogBuilder")
public final class TestGameFile {

    @lombok.Builder.Default
    private GameFileId id = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

    @lombok.Builder.Default
    private GameId gameId = new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281");

    @lombok.Builder.Default
    private GameProviderId gameProviderId = new GameProviderId("GOG");

    @lombok.Builder.Default
    private String originalGameTitle = "Game 1";

    @lombok.Builder.Default
    private String fileTitle = "Game 1 (Installer)";

    @lombok.Builder.Default
    private String version = "1.0.0";

    @lombok.Builder.Default
    private String url = "/downlink/some_game/some_file";

    @lombok.Builder.Default
    private String originalFileName = "game_1_installer.exe";

    @lombok.Builder.Default
    private FileSize size = new FileSize(5120L);

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

        public Builder dataFrom(DiscoveredFile discoveredFile) {
            return this.gameProviderId(discoveredFile.gameProviderId())
                    .originalGameTitle(discoveredFile.originalGameTitle())
                    .fileTitle(discoveredFile.fileTitle())
                    .version(discoveredFile.version())
                    .url(discoveredFile.url())
                    .originalFileName(discoveredFile.originalFileName())
                    .size(discoveredFile.size());
        }

        public GameFile build() {
            var temp = internalBuild();
            return new GameFile(
                    temp.id,
                    temp.gameId,
                    temp.gameProviderId,
                    temp.originalGameTitle,
                    temp.fileTitle,
                    temp.version,
                    temp.url,
                    temp.originalFileName,
                    temp.size,
                    temp.dateCreated,
                    temp.dateModified
            );
        }
    }
}
