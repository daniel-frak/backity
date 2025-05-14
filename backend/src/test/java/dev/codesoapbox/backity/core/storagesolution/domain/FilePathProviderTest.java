package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class FilePathProviderTest {

    private static final String PATH_TEMPLATE = "/test/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}";

    private FilePathProvider filePathProvider;

    private FakeUnixStorageSolution fakeUnixFileManager;

    @BeforeEach
    void setUp() {
        fakeUnixFileManager = new FakeUnixStorageSolution();
        filePathProvider = new FilePathProvider(PATH_TEMPLATE, fakeUnixFileManager);
    }

    @Test
    void shouldFixSeparatorCharInPathTemplate() {
        var wrongSeparator = "\\";
        if (wrongSeparator.equals(File.separator)) {
            wrongSeparator = "/";
        }

        String wrongPathTemplate = "one" + wrongSeparator + "two";
        filePathProvider = new FilePathProvider(wrongPathTemplate, fakeUnixFileManager);

        assertThat(filePathProvider.defaultPathTemplate).isEqualTo("one" + File.separator + "two")
                .isNotEqualTo(wrongPathTemplate);
    }

    @Test
    void shouldBuildFilePath() {
        var gameProviderId = new GameProviderId("someGameProviderId");
        var gameTitle = "someGameTitle";
        var fileName = "someFileName";

        String result = filePathProvider.buildUniqueFilePath(gameProviderId, gameTitle, fileName);

        String expectedPath = "/test/someGameProviderId/someGameTitle/" + extractFileName(result);

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
    }

    private String extractFileName(String result) {
        return result.substring(result.lastIndexOf("/") + 1);
    }

    @Test
    void shouldBuildFilePathWhenNoSeparatorFound() {
        var gameProviderId = new GameProviderId("someGameProviderId");
        var gameTitle = "some: GameTitle";
        var fileName = "someFileName";

        filePathProvider = new FilePathProvider("{FILENAME}", fakeUnixFileManager);

        String result = filePathProvider.buildUniqueFilePath(gameProviderId, gameTitle, fileName);

        String expectedPath = extractFileName(result);

        assertThat(result).doesNotContain(File.separator);
        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
        assertThat(fakeUnixFileManager.anyDirectoriesWereCreated()).isFalse();
    }

    @Test
    void shouldRemoveIllegalCharacters() {
        String charactersToRemove = "<>\"|?\n`';!@#$%^&*{}[]~";
        var gameProviderId = new GameProviderId("someGameProviderId" + charactersToRemove);
        var gameTitle = "some:\tGameTitle" + charactersToRemove;
        var fileName = "someFileName";

        String result = filePathProvider.buildUniqueFilePath(gameProviderId, gameTitle, fileName);

        String expectedPath = "/test/someGameProviderId/some - GameTitle/" + extractFileName(result);

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
    }

    @Test
    void shouldBuildUniqueFilePathGivenFileWithExtensionAlreadyExists() {
        var gameProviderId = new GameProviderId("someGameProviderId");
        var gameTitle = "someGameTitle";
        var fileName = "someFileName.txt";
        fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName.txt");

        String result = filePathProvider.buildUniqueFilePath(gameProviderId, gameTitle, fileName);

        String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_1.txt";

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
    }

    @Test
    void shouldBuildUniqueFilePathGivenSeveralFilesWithExtensionAlreadyExist() {
        var gameProviderId = new GameProviderId("someGameProviderId");
        var gameTitle = "someGameTitle";
        var fileName = "someFileName.txt";
        fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName.txt");
        fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName_1.txt");

        String result = filePathProvider.buildUniqueFilePath(gameProviderId, gameTitle, fileName);

        String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_2.txt";

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
    }

    @Test
    void shouldBuildUniqueFilePathGivenFileWithoutExtensionAlreadyExists() {
        var gameProviderId = new GameProviderId("someGameProviderId");
        var gameTitle = "someGameTitle";
        var fileName = "someFileName";
        fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName");

        String result = filePathProvider.buildUniqueFilePath(gameProviderId, gameTitle, fileName);

        String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_1";

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
    }

    @Test
    void shouldBuildUniqueFilePathGivenSeveralFilesWithoutExtensionAlreadyExist() {
        var gameProviderId = new GameProviderId("someGameProviderId");
        var gameTitle = "someGameTitle";
        var fileName = "someFileName";
        fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName");
        fakeUnixFileManager.createFile("/test/someGameProviderId/someGameTitle/someFileName_1");

        String result = filePathProvider.buildUniqueFilePath(gameProviderId, gameTitle, fileName);

        String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName_2";

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
    }
}