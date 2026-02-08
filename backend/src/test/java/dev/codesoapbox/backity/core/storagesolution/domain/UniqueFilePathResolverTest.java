package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.CouldNotResolveUniqueFilePathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UniqueFilePathResolverTest {

    private static final String PATH_TEMPLATE = "/test/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}";

    private UniqueFilePathResolver uniqueFilePathResolver;

    private FakeUnixStorageSolution fakeUnixFileManager;

    @BeforeEach
    void setUp() {
        fakeUnixFileManager = new FakeUnixStorageSolution();
        uniqueFilePathResolver = new UniqueFilePathResolver();
    }

    private String toUnixPath(String result) {
        return result.replace("\\", "/");
    }

    @Nested
    class BasicFilePathConstruction {

        @Test
        void shouldResolveFilePath() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldResolveFilePathWhenNoSeparatorInPathTemplate() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve("{FILENAME}", sourceFile, fakeUnixFileManager);

            String expectedPath = "someFileName";
            assertThat(result).doesNotContain(File.separator);
            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldResolveValidFilePathGivenWrongSeparatorInPathTemplate() {
            String wrongSeparator = getWrongSeparator();
            String wrongPathTemplate = "{TITLE}" + wrongSeparator + "{FILENAME}";
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(wrongPathTemplate, sourceFile, fakeUnixFileManager);

            String expectedPath = "someGameTitle" + File.separator + "someFileName";
            assertThat(result).isEqualTo(expectedPath);
        }

        private String getWrongSeparator() {
            var wrongSeparator = "\\";
            if (wrongSeparator.equals(File.separator)) {
                wrongSeparator = "/";
            }
            return wrongSeparator;
        }
    }

    @Nested
    class FilePathSanitization {

        @Test
        void shouldRemoveIllegalCharactersFromPathTemplate() {
            var charactersToRemove = "<>\"|?\n`';!@#$%^&*[]~";
            var pathTemplate = "/test" + charactersToRemove + "/{FILENAME}";
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(pathTemplate, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldReplaceIllegalCharactersFromPathTemplate() {
            var pathTemplate = "/some:test\tfolder 1/{FILENAME}";
            uniqueFilePathResolver = new UniqueFilePathResolver();
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .originalFileName("someFileName")
                    .build();

            String result = uniqueFilePathResolver.resolve(pathTemplate, sourceFile, fakeUnixFileManager);

            String expectedPath = "/some -test folder 1/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldRemoveIllegalCharactersEachPlaceholder() {
            String charactersToRemove = "<>\"|?\n`';!@#$%^&*[]~{}";
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId" + charactersToRemove))
                    .originalGameTitle("someGameTitle" + charactersToRemove)
                    .originalFileName("someFileName" + charactersToRemove)
                    .build();

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldReplaceIllegalCharactersEachPlaceholder() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("some:Game\tProviderId 1"))
                    .originalGameTitle("some:Game\tTitle 1")
                    .originalFileName("some:File\tName 1")
                    .build();

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/some -Game ProviderId 1/some -Game Title 1/some -File Name 1";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }
    }

    @Nested
    class UniqueFilePathConstruction {

        @Test
        void shouldResolveUniqueFilePathGivenFileWithExtensionAlreadyExists() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName.txt")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName.txt");

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_1.txt";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldResolveUniqueFilePathGivenSeveralFilesWithExtensionAlreadyExist() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName.txt")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName.txt");
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName_1.txt");

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_2.txt";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldResolveUniqueFilePathGivenFileWithoutExtensionAlreadyExists() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName");

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_1";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldResolveUniqueFilePathGivenSeveralFilesWithoutExtensionAlreadyExist() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName");
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName_1");

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_2";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @Test
        void shouldThrowAfterFailingToFindUniqueFilePathTooManyTimes() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName.exe")
                    .build();
            fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName.exe");
            for (int i = 0; i < 999; i++) {
                fakeUnixFileManager.createFile(
                        "/test/someGameProviderId/someGameTitle/someFileName_" + i + ".exe");
            }

            assertThatThrownBy(() -> uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager))
                    .isInstanceOf(CouldNotResolveUniqueFilePathException.class)
                    .hasMessageContaining("someGameTitle")
                    .hasMessageContaining("someFileName.exe");
        }
    }
}