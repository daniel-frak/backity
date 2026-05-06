package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.FileCouldNotBeDeletedException;
import dev.codesoapbox.backity.testing.s3.annotations.S3RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@S3RepositoryTest(buckets = S3StorageSolutionIT.BUCKET_NAME,
        filesToUpload = {
                @S3RepositoryTest.FileToUpload(
                        bucket = S3StorageSolutionIT.BUCKET_NAME,
                        key = S3StorageSolutionIT.S3_FILE_1_KEY,
                        fileContent = S3StorageSolutionIT.S3_FILE_1_CONTENT
                ),
                @S3RepositoryTest.FileToUpload(
                        bucket = S3StorageSolutionIT.BUCKET_NAME,
                        key = S3StorageSolutionIT.S3_FILE_2_KEY,
                        fileContent = S3StorageSolutionIT.S3_FILE_2_CONTENT
                ),
                @S3RepositoryTest.FileToUpload(
                        bucket = S3StorageSolutionIT.BUCKET_NAME,
                        key = S3StorageSolutionIT.S3_FILE_WITHOUT_EXTENSION_KEY,
                        fileContent = S3StorageSolutionIT.S3_FILE_WITHOUT_EXTENSION_CONTENT
                )
        })
class S3StorageSolutionIT {

    protected static final String BUCKET_NAME = "backity";
    protected static final String S3_FILE_PATH = "path/to/game/";

    protected static final String S3_FILE_1_NAME = "existing_source_file_1.txt";
    protected static final String S3_FILE_1_KEY = S3_FILE_PATH + S3_FILE_1_NAME;
    protected static final String S3_FILE_1_CONTENT = "This is a mock source file (1).";

    protected static final String S3_FILE_2_NAME_BASE = "existing_source_file_2";
    protected static final String S3_FILE_2_NAME_EXTENSION = ".txt";
    protected static final String S3_FILE_2_NAME = S3_FILE_2_NAME_BASE + S3_FILE_2_NAME_EXTENSION;
    protected static final String S3_FILE_2_KEY = S3_FILE_PATH + S3_FILE_2_NAME;
    protected static final String S3_FILE_2_CONTENT = "This is a mock source file (2).";

    protected static final String S3_FILE_WITHOUT_EXTENSION_NAME = "existing_source_file_without_extension";
    protected static final String S3_FILE_WITHOUT_EXTENSION_KEY = S3_FILE_PATH + S3_FILE_WITHOUT_EXTENSION_NAME;
    protected static final String S3_FILE_WITHOUT_EXTENSION_CONTENT = "This is a mock source file (3).";

    private static final int BUFFER_SIZE_IN_BYTES = 5_000;

    private S3StorageSolution s3StorageSolution;

    @BeforeEach
    void setUp(S3Client s3Client) {
        s3StorageSolution = new S3StorageSolution(s3Client, BUCKET_NAME, BUFFER_SIZE_IN_BYTES);
    }

    private String readContent(FileResource fileResource) {
        return new BufferedReader(new InputStreamReader(fileResource.inputStream()))
                .lines().collect(Collectors.joining("\n"));
    }

    @Nested
    class GetId {

        @Test
        void shouldGetId() {
            StorageSolutionId result = s3StorageSolution.getId();

            StorageSolutionId expectedResult = new StorageSolutionId("S3");
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class GetSeparator {

        @Test
        void shouldGetSeparator() {
            String result = s3StorageSolution.getSeparator();

            assertThat(result).isEqualTo("/");
        }
    }

    @Nested
    class GetOutputStream {

        @Test
        void ShouldReturnValidOutputStream() throws IOException {
            var filePath = new FilePath(S3_FILE_PATH + "someFile");
            var fileContent = "Test Data";

            try (OutputStream outputStream = s3StorageSolution.getOutputStream(filePath)) {
                outputStream.write(fileContent.getBytes());
            }

            assertThatDataWasWrittenToS3(filePath, fileContent);
        }

        private void assertThatDataWasWrittenToS3(FilePath filePath, String fileContent) throws IOException {
            try (var fileResource = s3StorageSolution.getFileResource(filePath)) {
                String readData = readContent(fileResource);
                assertThat(readData).isEqualTo(fileContent);
            }
        }

        @Test
        void shouldFailGivenFileAlreadyExists() {
            var filePath = new FilePath(S3_FILE_1_KEY);

            assertThatThrownBy(() -> s3StorageSolution.getOutputStream(filePath))
                    .isInstanceOf(FileAlreadyExistsException.class);
        }
    }

    @Nested
    class DeleteIfExists {

        @Test
        void shouldDeleteGivenFileExists() {
            var filePath = new FilePath(S3_FILE_1_KEY);

            s3StorageSolution.deleteIfExists(filePath);

            assertFileDoesNotExist(filePath);
        }

        private void assertFileDoesNotExist(FilePath filePath) {
            assertThatThrownBy(() -> s3StorageSolution.getFileResource(filePath).close())
                    .isInstanceOf(FileNotFoundException.class);
        }

        @SuppressWarnings("unchecked")
        @Test
        void shouldThrowGivenException() {
            S3Client s3Client = mock(S3Client.class);
            var expectedCause = new RuntimeException("Test exception");
            when(s3Client.deleteObject(any(Consumer.class)))
                    .thenThrow(expectedCause);
            s3StorageSolution = new S3StorageSolution(s3Client, BUCKET_NAME, BUFFER_SIZE_IN_BYTES);
            var filePath = new FilePath(S3_FILE_1_KEY);

            assertThatThrownBy(() -> s3StorageSolution.deleteIfExists(filePath))
                    .isInstanceOf(FileCouldNotBeDeletedException.class)
                    .hasMessageContaining(S3_FILE_1_KEY)
                    .hasCause(expectedCause);
        }

        @Test
        void shouldDoNothingGivenFileDoesNotExist() {
            var filePath = new FilePath("nonexistent_file");
            assertThatCode(() -> s3StorageSolution.deleteIfExists(filePath))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class GetSizeInBytes {

        @Test
        void shouldReturnCorrectSize() {
            long expectedSizeInBytes = S3_FILE_1_CONTENT.length();
            var filePath = new FilePath(S3_FILE_1_KEY);

            long result = s3StorageSolution.getSizeInBytes(filePath);

            assertThat(result).isEqualTo(expectedSizeInBytes);
        }

        @Test
        void shouldReturnZeroGivenFileNotFound() {
            var filePath = new FilePath("nonexistentfile.txt");

            long result = s3StorageSolution.getSizeInBytes(filePath);

            assertThat(result).isZero();
        }
    }

    @Nested
    class GetFileResource {

        @Test
        void shouldReturnFileResourceGivenFileExists() throws FileNotFoundException {
            var filePath = new FilePath(S3_FILE_1_KEY);
            FileResource fileResource = s3StorageSolution.getFileResource(filePath);

            assertThat(fileResource).isNotNull();
            assertThat(fileResource.sizeInBytes()).isPositive();
            assertThat(fileResource.fileName()).isEqualTo(S3_FILE_1_NAME);
            assertThat(readContent(fileResource)).isEqualTo(S3_FILE_1_CONTENT);
            assertThatCode(fileResource::close)
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldThrowGivenFileNotFound() {
            var filePath = new FilePath("nonexistentfile.txt");

            assertThatThrownBy(() -> s3StorageSolution.getFileResource(filePath))
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage("File not found: " + filePath);
        }

        @Test
        void ShouldThrowGivenFileIsDirectory() {
            var filePath = new FilePath(S3_FILE_PATH);
            assertThatThrownBy(() -> s3StorageSolution.getFileResource(filePath))
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage("File not found: " + S3_FILE_PATH);
        }
    }

    @Nested
    class FileExists {

        @Test
        void shouldReturnTrueGivenFileExists() {
            var filePath = new FilePath(S3_FILE_1_KEY);
            boolean result = s3StorageSolution.fileExists(filePath);

            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnTrueGivenFileDoesNotExist() {
            var nonExistentFilePath = new FilePath("someFilePath");

            boolean result = s3StorageSolution.fileExists(nonExistentFilePath);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class GetStatus {

        @Test
        void shouldReturnConnectedGivenBucketCanBeAccessed() {
            StorageSolutionStatus result = s3StorageSolution.getStatus();

            assertThat(result).isEqualTo(StorageSolutionStatus.CONNECTED);
        }

        @Test
        void shouldReturnNotConnectedGivenBucketCannotBeAccessed() {
            S3StorageSolution failingStorageSolution = createNotConnectedStorageSolution();

            StorageSolutionStatus result = failingStorageSolution.getStatus();

            assertThat(result).isEqualTo(StorageSolutionStatus.NOT_CONNECTED);
        }

        @SuppressWarnings("unchecked")
        private S3StorageSolution createNotConnectedStorageSolution() {
            S3Client failingClient = mock(S3Client.class);
            when(failingClient.headBucket(any(Consumer.class)))
                    .thenThrow(new RuntimeException("Simulated connectivity failure"));

            return new S3StorageSolution(failingClient, BUCKET_NAME, BUFFER_SIZE_IN_BYTES);
        }
    }
}