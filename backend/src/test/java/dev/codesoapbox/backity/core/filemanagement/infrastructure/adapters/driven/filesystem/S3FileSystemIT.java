package dev.codesoapbox.backity.core.filemanagement.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.filemanagement.domain.exceptions.FileCouldNotBeDeletedException;
import dev.codesoapbox.backity.testing.s3.annotations.S3RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@S3RepositoryTest(buckets = S3FileSystemIT.BUCKET_NAME,
        filesToUpload = {
                @S3RepositoryTest.FileToUpload(
                        bucket = S3FileSystemIT.BUCKET_NAME,
                        key = S3FileSystemIT.S3_FILE_1_KEY,
                        fileContent = S3FileSystemIT.S3_FILE_1_CONTENT
                ),
                @S3RepositoryTest.FileToUpload(
                        bucket = S3FileSystemIT.BUCKET_NAME,
                        key = S3FileSystemIT.S3_FILE_2_KEY,
                        fileContent = S3FileSystemIT.S3_FILE_2_CONTENT
                ),
                @S3RepositoryTest.FileToUpload(
                        bucket = S3FileSystemIT.BUCKET_NAME,
                        key = S3FileSystemIT.S3_FILE_WITHOUT_EXTENSION_KEY,
                        fileContent = S3FileSystemIT.S3_FILE_WITHOUT_EXTENSION_CONTENT
                )
        })
class S3FileSystemIT {

    protected static final String BUCKET_NAME = "backity";
    protected static final String S3_FILE_PATH = "path/to/game/";

    protected static final String S3_FILE_1_NAME = "existing_game_file_1.txt";
    protected static final String S3_FILE_1_KEY = S3_FILE_PATH + S3_FILE_1_NAME;
    protected static final String S3_FILE_1_CONTENT = "This is a mock game file (1).";

    protected static final String S3_FILE_2_NAME_BASE = "existing_game_file_2";
    protected static final String S3_FILE_2_NAME_EXTENSION = ".txt";
    protected static final String S3_FILE_2_NAME = S3_FILE_2_NAME_BASE + S3_FILE_2_NAME_EXTENSION;
    protected static final String S3_FILE_2_KEY = S3_FILE_PATH + S3_FILE_2_NAME;
    protected static final String S3_FILE_2_CONTENT = "This is a mock game file (2).";

    protected static final String S3_FILE_WITHOUT_EXTENSION_NAME = "existing_game_file_without_extension";
    protected static final String S3_FILE_WITHOUT_EXTENSION_KEY = S3_FILE_PATH + S3_FILE_WITHOUT_EXTENSION_NAME;
    protected static final String S3_FILE_WITHOUT_EXTENSION_CONTENT = "This is a mock game file (3).";

    private static final int BUFFER_SIZE_IN_BYTES = 5_000;

    private S3FileSystem s3FileSystem;

    @BeforeEach
    void setUp(S3Client s3Client) {
        s3FileSystem = new S3FileSystem(s3Client, BUCKET_NAME, BUFFER_SIZE_IN_BYTES);
    }

    @Test
    void shouldGetSeparator() {
        String result = s3FileSystem.getSeparator();

        assertThat(result).isEqualTo("/");
    }

    @Test
    void getOutputStreamShouldReturnValidOutputStream() throws IOException {
        var filePath = S3_FILE_PATH + "someFile";
        var fileContent = "Test Data";

        try (OutputStream outputStream = s3FileSystem.getOutputStream(filePath)) {
            outputStream.write(fileContent.getBytes());
        }

        assertThatDataWasWrittenToS3(filePath, fileContent);
    }

    private void assertThatDataWasWrittenToS3(String key, String fileContent) throws IOException {
        try (var fileResource = s3FileSystem.getFileResource(key)) {
            String readData = readContent(fileResource);
            assertThat(readData).isEqualTo(fileContent);
        }
    }

    private String readContent(FileResource fileResource) {
        return new BufferedReader(new InputStreamReader(fileResource.inputStream()))
                .lines().collect(Collectors.joining("\n"));
    }

    @Test
    void getOutputStreamShouldFailGivenFileAlreadyExists() {
        assertThatThrownBy(() -> s3FileSystem.getOutputStream(S3_FILE_1_KEY))
                .isInstanceOf(FileAlreadyExistsException.class);
    }

    @Test
    void shouldDeleteGivenFileExists() {
        s3FileSystem.deleteIfExists(S3_FILE_1_KEY);

        assertFileDoesNotExist(S3_FILE_1_KEY);
    }

    private void assertFileDoesNotExist(String key) {
        assertThatThrownBy(() -> s3FileSystem.getFileResource(key).close())
                .isInstanceOf(FileNotFoundException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    void deleteIfExistsShouldThrowGivenException() {
        S3Client s3Client = mock(S3Client.class);
        var expectedCause = new RuntimeException("Test exception");
        when(s3Client.deleteObject(any(Consumer.class)))
                .thenThrow(expectedCause);
        s3FileSystem = new S3FileSystem(s3Client, BUCKET_NAME, BUFFER_SIZE_IN_BYTES);

        assertThatThrownBy(() -> s3FileSystem.deleteIfExists(S3_FILE_1_KEY))
                .isInstanceOf(FileCouldNotBeDeletedException.class)
                .hasMessageContaining(S3_FILE_1_KEY)
                .hasCause(expectedCause);
    }

    @Test
    void deleteShouldDoNothingGivenFileDoesNotExist() {
        assertThatCode(() -> s3FileSystem.deleteIfExists("nonexistent_file"))
                .doesNotThrowAnyException();
    }

    @Test
    void getSizeInBytesShouldReturnCorrectSize() {
        long expectedSizeInBytes = S3_FILE_1_CONTENT.length();

        long result = s3FileSystem.getSizeInBytes(S3_FILE_1_KEY);

        assertThat(result).isEqualTo(expectedSizeInBytes);
    }

    @Test
    void getSizeInBytesShouldReturnZeroGivenFileNotFound() {
        long result = s3FileSystem.getSizeInBytes("nonexistentfile.txt");

        assertThat(result).isZero();
    }

    @Test
    void getFileResourceShouldReturnFileResourceGivenFileExists() throws FileNotFoundException {
        FileResource fileResource = s3FileSystem.getFileResource(S3_FILE_1_KEY);

        assertThat(fileResource).isNotNull();
        assertThat(fileResource.sizeInBytes()).isPositive();
        assertThat(fileResource.fileName()).isEqualTo(S3_FILE_1_NAME);
        assertThat(readContent(fileResource)).isEqualTo(S3_FILE_1_CONTENT);
        assertThatCode(fileResource::close)
                .doesNotThrowAnyException();
    }

    @Test
    void getFileResourceShouldThrowGivenFileNotFound() {
        String filePath = "nonexistentfile.txt";

        assertThatThrownBy(() -> s3FileSystem.getFileResource(filePath))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("File not found: " + filePath);
    }

    @Test
    void getFileResourceShouldThrowGivenFileIsDirectory() {
        assertThatThrownBy(() -> s3FileSystem.getFileResource(S3_FILE_PATH))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("File not found: " + S3_FILE_PATH);
    }

    @Test
    void fileExistsShouldReturnTrueGivenFileExists() {
        boolean result = s3FileSystem.fileExists(S3_FILE_1_KEY);

        assertThat(result).isTrue();
    }

    @Test
    void fileExistsShouldReturnTrueGivenFileDoesNotExist() {
        String nonExistentFilePath = "someFilePath";

        boolean result = s3FileSystem.fileExists(nonExistentFilePath);

        assertThat(result).isFalse();
    }
}