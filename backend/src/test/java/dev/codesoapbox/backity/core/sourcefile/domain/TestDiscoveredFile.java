package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.game.domain.GameTitle;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "minimalGogBuilder")
public class TestDiscoveredFile {

    @lombok.Builder.Default
    private GameProviderId gameProviderId = new GameProviderId("GOG");

    @lombok.Builder.Default
    private GameTitle originalGameTitle = new GameTitle("Game 1");

    @lombok.Builder.Default
    private FileTitle fileTitle = new FileTitle("Game 1 (Installer)");

    @lombok.Builder.Default
    private FileVersion version = new FileVersion("1.0.0");

    @lombok.Builder.Default
    private SourceFileUrl url = new SourceFileUrl("/downlink/some_game/some_file");

    @lombok.Builder.Default
    private String originalFileName = "game_1_installer.exe";

    @lombok.Builder.Default
    private FileSize size = new FileSize(5120L);

    public static DiscoveredFile minimalGog() {
        return minimalGogBuilder().build();
    }

    public static class Builder {

        public DiscoveredFile build() {
            TestDiscoveredFile temp = internalBuild();

            return new DiscoveredFile(
                    temp.gameProviderId,
                    temp.originalGameTitle,
                    temp.fileTitle,
                    temp.version,
                    temp.url,
                    temp.originalFileName,
                    temp.size
            );
        }
    }
}