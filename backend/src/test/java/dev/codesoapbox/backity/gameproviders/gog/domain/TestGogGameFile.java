package dev.codesoapbox.backity.gameproviders.gog.domain;

import lombok.Builder;

@Builder(builderClassName = "Builder", builderMethodName = "minimalBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public class TestGogGameFile {

    @lombok.Builder.Default
    private String version = "unknown";

    @lombok.Builder.Default
    private String manualUrl = "/downlink/some_game/some_file";

    @lombok.Builder.Default
    private String title = "Game 1 (Installer)";

    @lombok.Builder.Default
    private String size = "1 MB";

    @lombok.Builder.Default
    private String fileName = "game_1_installer.exe";

    public static GogGameFile minimal() {
        return minimalBuilder().build();
    }

    public static TestGogGameFile.Builder minimalBuilder() {
        return new Builder();
    }

    public static class Builder {

        public GogGameFile build() {
            TestGogGameFile temp = internalBuilder();
            return new GogGameFile(temp.version, temp.manualUrl, temp.title, temp.size, temp.fileName);
        }
    }
}