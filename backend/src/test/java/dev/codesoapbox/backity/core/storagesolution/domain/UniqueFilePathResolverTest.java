package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.CouldNotResolveUniqueFilePathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UniqueFilePathResolverTest {

    private static final PathTemplate PATH_TEMPLATE =
            new PathTemplate("/test/{GAME_PROVIDER_ID}/{GAME_TITLE}/{FILENAME}");

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
                    .originalFileName("someFileName.exe")
                    .build();
            createFileSeveralTimes(999,
                    "/test/someGameProviderId/someGameTitle/someFileName", ".exe");

            String result = uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager);

            String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_999.exe";

            assertThat(toUnixPath(result)).isEqualTo(expectedPath);
        }

        @SuppressWarnings("SameParameterValue")
        private void createFileSeveralTimes(int times, String pathWithoutExtension, String extension) {
            fakeUnixFileManager.createFile(pathWithoutExtension + extension);
            for (int i = 1; i < times; i++) {
                fakeUnixFileManager.createFile(
                        "/test/someGameProviderId/someGameTitle/someFileName_" + i + ".exe");
            }
        }

        @Test
        void shouldThrowAfterFailingToFindUniqueFilePathTooManyTimes() {
            SourceFile sourceFile = TestSourceFile.gogBuilder()
                    .gameProviderId(new GameProviderId("someGameProviderId"))
                    .originalGameTitle("someGameTitle")
                    .originalFileName("someFileName.exe")
                    .build();
            createFileSeveralTimes(1000,
                    "/test/someGameProviderId/someGameTitle/someFileName", ".exe");

            assertThatThrownBy(() -> uniqueFilePathResolver.resolve(PATH_TEMPLATE, sourceFile, fakeUnixFileManager))
                    .isInstanceOf(CouldNotResolveUniqueFilePathException.class)
                    .hasMessageContaining("someGameTitle")
                    .hasMessageContaining("someFileName.exe");
        }
    }
}