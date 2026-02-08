package dev.codesoapbox.backity.gameproviders.gog.domain;

import lombok.Builder;

@Builder(builderClassName = "Builder", builderMethodName = "minimalBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public class TestGogFile {

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

    public static GogFile minimal() {
        return minimalBuilder().build();
    }

    public static TestGogFile.Builder minimalBuilder() {
        return new Builder();
    }

    public static class Builder {

        public GogFile build() {
            TestGogFile temp = internalBuilder();
            return new GogFile(temp.version, temp.manualUrl, temp.title, temp.size, temp.fileName);
        }
    }
}