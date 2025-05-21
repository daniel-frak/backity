package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSource;
import dev.codesoapbox.backity.core.gamefile.domain.TestFileSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class UniqueFilePathResolverTest {

    private static final String PATH_TEMPLATE = "/test/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}";

    private UniqueFilePathResolver uniqueFilePathResolver;

    private FakeUnixStorageSolution fakeUnixFileManager;

    @BeforeEach
    void setUp() {
        fakeUnixFileManager = new FakeUnixStorageSolution();
        uniqueFilePathResolver = new UniqueFilePathResolver(PATH_TEMPLATE, fakeUnixFileManager);
    }

    private String toUnixPath(String result) {
        return result.replace("\\", "/");
    }

    @Nested
    class PathTemplateSanitization {

        @Test
        void shouldFixSeparatorCharInPathTemplate() {
            var wrongSeparator = "\\";
            if (wrongSeparator.equals(File.separator)) {
                wrongSeparator = "/";
            }

            String wrongPathTemplate = "one" + wrongSeparator + "two";
            uniqueFilePathResolver = new UniqueFilePathResolver(wrongPathTemplate, fakeUnixFileManager);

            assertThat(uniqueFilePathResolver.defaultPathTemplate).isEqualTo("one" + File.separator + "two")
                    .isNotEqualTo(wrongPathTemplate);
        }
    }

    @Nested
    class BasicFilePathConstruction {

        @Test
        void shouldBuildFilePath() {
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someGameProviderId/someGameTitle/" + extractFileName(result);

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        private String extractFileName(String result) {
            return result.substring(result.lastIndexOf("/") + 1);
        }

        @Test
        void shouldBuildFilePathWhenNoSeparatorInPathTemplate() {
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();
            uniqueFilePathResolver = new UniqueFilePathResolver("{FILENAME}", fakeUnixFileManager);

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = extractFileName(result);

            assertThat(result).doesNotContain(File.separator);
            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
            assertThat(fakeUnixFileManager.anyDirectoriesWereCreated()).isFalse();
        }

        @Test
        void shouldRemoveOrReplaceIllegalCharacters() {
            String charactersToRemoveOrReplace = "<>\"|?\n`';!@#$%^&*{}[]~";
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId" + charactersToRemoveOrReplace))
                    .originalGameTitle("some:\tGameTitle")
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someGameProviderId/some - GameTitle/" + extractFileName(result);

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }
    }

    @Nested
    class UniqueFilePathConstruction {

        @Test
        void shouldBuildUniqueFilePathGivenFileWithExtensionAlreadyExists() {
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName.txt")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName.txt");

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_1.txt";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldBuildUniqueFilePathGivenSeveralFilesWithExtensionAlreadyExist() {
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName.txt")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName.txt");
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName_1.txt");

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_2.txt";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldBuildUniqueFilePathGivenFileWithoutExtensionAlreadyExists() {
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName");

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_1";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldBuildUniqueFilePathGivenSeveralFilesWithoutExtensionAlreadyExist() {
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName");
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName_1");

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_2";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }
    }
}