package dev.codesoapbox.backity.core.filemanagement.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class FilePathProviderTest {

    private static final String PATH_TEMPLATE = "/test/{SOURCE}/{TITLE}/{FILENAME}";

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
        var source = "someSource";
        var gameTitle = "some: GameTitle";

        String result = filePathProvider.createTemporaryFilePath(source, gameTitle);

        String expectedPath = "/test/someSource/some - GameTitle/" + extractFileName(result);

        assertThat(result.replace("\\", "/")).isEqualTo(expectedPath);
        assertThat(fakeUnixFileManager.directoryWasCreated("/test/someSource/some - GameTitle")).isTrue();
    }

    @Test
    void shouldCreateTemporaryFilePathWhenNoSeparatorFound() throws IOException {
        var source = "someSource";
        var gameTitle = "some: GameTitle";

        filePathProvider = new FilePathProvider("{FILENAME}", fakeUnixFileManager);

        String result = filePathProvider.createTemporaryFilePath(source, gameTitle);

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
}