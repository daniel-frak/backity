package dev.codesoapbox.backity.gameproviders.gog.domain;

import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import lombok.Builder;

import java.util.List;

import static java.util.Collections.emptyList;

@Builder(builderClassName = "Builder", builderMethodName = "minimalBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public class TestGogGameWithFiles {

    @lombok.Builder.Default
    private String title = "Test Game";

    private String backgroundImage;
    private String cdKey;
    private String textInformation;

    @lombok.Builder.Default
    private List<GogGameFile> files = emptyList();
    private String changelog;

    public static GogGameWithFiles minimal() {
        return minimalBuilder().build();
    }

    public static TestGogGameWithFiles.Builder minimalBuilder() {
        return new Builder();
    }

    public static GogGameWithFiles fullWithMinimalFile() {
        return fullBuilder().build();
    }

    public static TestGogGameWithFiles.Builder fullBuilder() {
        return minimalBuilder()
                .withFiles(List.of(TestGogGameFile.minimalBuilder().build()))
                .withBackgroundImage("//images-4.gog.com/somePath")
                .withCdKey("some-cd-key")
                .withTextInformation("Some text information")
                .withChangelog("Some changelog");
    }

    public static GogGameWithFiles fromSingleFile(GameProviderFile gameProviderFile) {
        return new GogGameWithFiles(
                gameProviderFile.originalGameTitle(),
                null,
                null,
                null,
                List.of(
                        new GogGameFile(
                                gameProviderFile.version(),
                                gameProviderFile.url(),
                                gameProviderFile.fileTitle(),
                                gameProviderFile.size().toString(),
                                gameProviderFile.originalFileName()
                        )
                ),
                null
        );
    }

    public static class Builder {

        public GogGameWithFiles build() {
            TestGogGameWithFiles temp = internalBuilder();
            return new GogGameWithFiles(temp.title, temp.backgroundImage, temp.cdKey, temp.textInformation,
                    temp.files, temp.changelog);
        }
    }
}