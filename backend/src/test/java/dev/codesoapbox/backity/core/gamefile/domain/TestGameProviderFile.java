package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild", builderMethodName = "gogBuilder")
public class TestGameProviderFile {

    @lombok.Builder.Default
    private GameProviderId gameProviderId = new GameProviderId("GOG");

    @lombok.Builder.Default
    private String originalGameTitle = "Game 1";

    @lombok.Builder.Default
    private String fileTitle = "Game 1 (Installer)";

    @lombok.Builder.Default
    private String version = "1.0.0";

    @lombok.Builder.Default
    private String url = "http://some.url";

    @lombok.Builder.Default
    private String originalFileName = "game_1_installer.exe";

    @lombok.Builder.Default
    private FileSize size = new FileSize(5120L);

    public static GameProviderFile gog() {
        return gogBuilder().build();
    }

    public static class Builder {

        public GameProviderFile build() {
            var temp = internalBuild();
            return new GameProviderFile(
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