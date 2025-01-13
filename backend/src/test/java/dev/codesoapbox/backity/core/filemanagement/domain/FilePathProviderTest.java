package dev.codesoapbox.backity.core.filemanagement.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class FilePathProviderTest {

    private static final String PATH_TEMPLATE = "/test/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}";

    private FilePathProvider filePathProvider;

    private FakeUnixFileManager fakeUnixFileManager;

    @BeforeEach
    void setUp() {
        fakeUnixFileManager = new FakeUnixFileManager();
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
    void shouldCreateTemporaryFilePath() throws IOException {
        var gameProviderId = new GameProviderId( "someGameProviderId");
        var gameTitle = "someGameTitle";

        String result = filePathProvider.createTemporaryFilePath(gameProviderId, gameTitle);

        String expectedPath = "/test/someGameProviderId/someGameTitle/" + extractFileName(result);

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
        assertThat(fakeUnixFileManager.directoryWasCreated("/test/someGameProviderId/someGameTitle")).isTrue();
    }

    @Test
    void shouldCreateTemporaryFilePathWhenNoSeparatorFound() throws IOException {
        var gameProviderId = new GameProviderId("someGameProviderId");
        var gameTitle = "some: GameTitle";

        filePathProvider = new FilePathProvider("{FILENAME}", fakeUnixFileManager);

        String result = filePathProvider.createTemporaryFilePath(gameProviderId, gameTitle);

        String expectedPath = extractFileName(result);

        assertThat(result).doesNotContain(File.separator);
        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
        assertThat(fakeUnixFileManager.anyDirectoriesWereCreated()).isFalse();
    }

    private String extractFileName(String result) {
        Matcher matcher = Pattern.compile("TEMP_\\S+$").matcher(result);

        if (!matcher.find()) {
            throw new AssertionFailedError("File name does not start with 'TEMP_': " + result);
        }

        return matcher.group();
    }

    @Test
    void shouldRemoveIllegalCharacters() throws IOException {
        String charactersToRemove = "<>\"|?\n`';!@#$%^&*{}[]~";
        var gameProviderId = new GameProviderId("someGameProviderId" + charactersToRemove);
        var gameTitle = "some:\tGameTitle" + charactersToRemove;

        String result = filePathProvider.createTemporaryFilePath(gameProviderId, gameTitle);

        String expectedPath = "/test/someGameProviderId/some - GameTitle/" + extractFileName(result);

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
        assertThat(fakeUnixFileManager.directoryWasCreated("/test/someGameProviderId/some - GameTitle")).isTrue();

    }
}