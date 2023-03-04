package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.downloading.fakes.FakeUnixFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals("one" + File.separator + "two", filePathProvider.defaultPathTemplate);
        assertNotEquals(wrongPathTemplate, filePathProvider.defaultPathTemplate);
    }

    @Test
    void shouldCreateTemporaryFilePath() throws IOException {
        var source = "someSource";
        var gameTitle = "some: GameTitle";

        String result = filePathProvider.createTemporaryFilePath(source, gameTitle);

        String expectedPath = "/test/someSource/some - GameTitle/" + extractFileName(result);

        assertEquals(expectedPath, result.replace("\\", "/"));
        assertTrue(fakeUnixFileManager.directoryWasCreated("/test/someSource/some - GameTitle"));
    }

    @Test
    void shouldCreateTemporaryFilePathWhenNoSeparatorFound() throws IOException {
        var source = "someSource";
        var gameTitle = "some: GameTitle";

        filePathProvider = new FilePathProvider("{FILENAME}", fakeUnixFileManager);

        String result = filePathProvider.createTemporaryFilePath(source, gameTitle);

        String expectedPath = extractFileName(result);

        assertFalse(result.contains(File.separator));
        assertEquals(expectedPath, result.replace("\\", "/"));
        assertFalse(fakeUnixFileManager.anyDirectoriesWereCreated());
    }

    private String extractFileName(String result) {
        Matcher matcher = Pattern.compile("TEMP_\\S+$").matcher(result);

        if (!matcher.find()) {
            throw new AssertionFailedError("File name does not start with 'TEMP_': " + result);
        }

        return matcher.group();
    }
}