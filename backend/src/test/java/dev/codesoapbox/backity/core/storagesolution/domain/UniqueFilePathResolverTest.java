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

    @Test
    void shouldFixSeparatorCharInPathTemplate() {
        String wrongSeparator = getWrongSeparator();
        var wrongPathTemplate = "one" + wrongSeparator + "two";

        uniqueFilePathResolver = new UniqueFilePathResolver(wrongPathTemplate, fakeUnixFileManager);

        assertThat(uniqueFilePathResolver.defaultPathTemplate).isEqualTo("one" + File.separator + "two")
                .isNotEqualTo(wrongPathTemplate);
    }

    private String getWrongSeparator() {
        var wrongSeparator = "\\";
        if (wrongSeparator.equals(File.separator)) {
            wrongSeparator = "/";
        }
        return wrongSeparator;
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

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
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

            String expectedPath = "someFileName";

            assertThat(result).doesNotContain(File.separator);
            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
            assertThat(fakeUnixFileManager.anyDirectoriesWereCreated()).isFalse();
        }
    }

    @Nested
    class FilePathSanitization {

        @Test
        void shouldRemoveIllegalCharactersFromPathTemplate() {
            var charactersToRemove = "<>\"|?\n`';!@#$%^&*[]~";
            var pathTemplate = "/test" + charactersToRemove + "/{FILENAME}";
            uniqueFilePathResolver = new UniqueFilePathResolver(pathTemplate, fakeUnixFileManager);
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldReplaceIllegalCharactersFromPathTemplate() {
            var pathTemplate = "/some:test\tfolder 1/{FILENAME}";
            uniqueFilePathResolver = new UniqueFilePathResolver(pathTemplate, fakeUnixFileManager);
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/some -test folder 1/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldRemoveIllegalCharactersEachPlaceholder() {
            String charactersToRemove = "<>\"|?\n`';!@#$%^&*[]~{}";
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId" + charactersToRemove))
                    .originalGameTitle("someGameTitle" + charactersToRemove)
                    .originalFileName("someFileName" + charactersToRemove)
                    .build();

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldReplaceIllegalCharactersEachPlaceholder() {
            FileSource fileSource = TestFileSource.minimalGogBuilder()
                    .gameProviderId(new GameProviderId("some:Game\tProviderId 1"))
                    .originalGameTitle("some:Game\tTitle 1")
                    .originalFileName("some:File\tName 1")
                    .build();

            String result = uniqueFilePathResolver.resolve(fileSource);

            String expectedPath = "/test/some -Game ProviderId 1/some -Game Title 1/some -File Name 1";

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