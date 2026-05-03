package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.FileCouldNotBeDeletedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class LocalFileSystemStorageSolutionIT {

    private LocalFileSystemStorageSolution localFileSystem;

    @BeforeEach
    void setUp() {
        localFileSystem = new LocalFileSystemStorageSolution();
    }

    @Nested
    class GetId {

        @Test
        void shouldReturnId() {
            StorageSolutionId result = localFileSystem.getId();

            StorageSolutionId expectedResult = new StorageSolutionId("LOCAL_FILE_SYSTEM");
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class DeleteIfExists {

        @Test
        void shouldDeleteGivenExists(@TempDir Path tempDir) throws IOException {
            String filePath = tempDir + File.separator + "someFile";

            var existingFileCreated = new File(filePath).createNewFile();

            localFileSystem.deleteIfExists(filePath);

            var fileExists = Files.exists(Path.of(filePath));

            assertThat(existingFileCreated).isTrue();
            assertThat(fileExists).isFalse();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test
        void shouldThrowGivenException(@TempDir Path tempDir) throws IOException {
            String filePath = tempDir + File.separator + "someFile";
            var existingFile = new File(filePath);
            existingFile.createNewFile();
            tempDir.toFile().setWritable(false); // This will cause an IOException
            Files.setAttribute(existingFile.toPath(), "dos:readonly", true); // Workaround for Windows

            try {
                assertThatThrownBy(() -> localFileSystem.deleteIfExists(filePath))
                        .isInstanceOf(FileCouldNotBeDeletedException.class)
                        .hasMessageContaining(filePath)
                        .hasCauseInstanceOf(IOException.class);
            } finally {
                tempDir.toFile().setWritable(true);
            }
        }

        @Test
        void shouldNotDeleteGivenDoesNotExist(@TempDir Path tempDir) {
            String filePath = tempDir + File.separator + "someFile";

            assertThatCode(() -> localFileSystem.deleteIfExists(filePath))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class GetOutputStream {

        @Test
        void shouldReturnValidOutputStream(@TempDir Path tempDir) throws IOException {
            var filePath = tempDir + File.separator + "someFile";
            var fileContent = "Test Data";

            try (OutputStream outputStream = localFileSystem.getOutputStream(filePath)) {
                outputStream.write(fileContent.getBytes());
            }

            assertThatDataWasWrittenToDisk(filePath, fileContent);
        }

        private void assertThatDataWasWrittenToDisk(String filePath, String fileContent) throws IOException {
            byte[] readBytes = Files.readAllBytes(Path.of(filePath));
            String readData = new String(readBytes);
            assertThat(readData).isEqualTo(fileContent);
        }

        @Test
        void shouldReturnValidOutputStreamWithSubfolders(@TempDir Path tempDir) throws IOException {
            var filePath = tempDir + File.separator + "subfolder" + File.separator + "someFile";
            var fileContent = "Test Data";

            try (OutputStream outputStream = localFileSystem.getOutputStream(filePath)) {
                outputStream.write(fileContent.getBytes());
            }

            assertThatDataWasWrittenToDisk(filePath, fileContent);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test
        void shouldFailGivenFileAlreadyExists(@TempDir Path tempDir) throws IOException {
            String filePath = tempDir + File.separator + "someFile";
            new File(filePath).createNewFile();

            assertThatThrownBy(() -> localFileSystem.getOutputStream(filePath))
                    .isInstanceOf(FileAlreadyExistsException.class);
        }
    }

    @Nested
    class GetSeparator {

        @Test
        void shouldReturnFileSeparator() {
            String result = localFileSystem.getSeparator();

            assertThat(result).isEqualTo(File.separator);
        }
    }

    @Nested
    class GetSizeInBytes {

        @Test
        void shouldReturnCorrectSize(@TempDir Path tempDir) throws IOException {
            String filePath = tempDir + File.separator + "someFile";
            String testData = "Test Data";
            try (var writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(testData);
            }

            var sizeInBytes = localFileSystem.getSizeInBytes(filePath);

            assertThat(sizeInBytes).isEqualTo(9);
        }
    }

    @Nested
    class GetFileResource {

        @Test
        void shouldReturnFileResourceGivenFileExists(@TempDir Path tempDir) throws IOException {
            String fileName = "testfile.txt";
            Path tempFile = tempDir.resolve(fileName);
            Files.write(tempFile, "Test content".getBytes());

            try (FileResource fileResource = localFileSystem.getFileResource(tempFile.toString())) {
                assertThat(fileResource).isNotNull();
                assertThat(fileResource.sizeInBytes()).isPositive();
                assertThat(fileResource.fileName()).isEqualTo(fileName);
                assertThatCode(fileResource::close)
                        .doesNotThrowAnyException();
            }
        }

        @Test
        void shouldThrowGivenFileNotFound() {
            String filePath = "nonexistentfile.txt";

            assertThatThrownBy(() -> localFileSystem.getFileResource(filePath).close())
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage("File not found: " + filePath);
        }

        @Test
        void shouldThrowGivenFileIsDirectory(@TempDir Path tempDir) throws IOException {
            Path tempDirectory = tempDir.resolve("tempDirectory");
            Files.createDirectory(tempDirectory);

            assertThatThrownBy(() -> localFileSystem.getFileResource(tempDirectory.toString()).close())
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage("File not found: " + tempDirectory);
        }
    }

    @Nested
    class FileExists {

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test
        void shouldReturnTrueGivenFileExists(@TempDir Path tempDir) throws IOException {
            String filePath = tempDir + File.separator + "someFile";
            new File(filePath).createNewFile();

            boolean result = localFileSystem.fileExists(filePath);

            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnFalseGivenFileDoesNotExist(@TempDir Path tempDir) {
            String nonExistentFilePath = tempDir + File.separator + "someFile";

            boolean result = localFileSystem.fileExists(nonExistentFilePath);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class GetStatus {

        @Test
        void shouldReturnConnected() {
            StorageSolutionStatus result = localFileSystem.getStatus();

            assertThat(result).isEqualTo(StorageSolutionStatus.CONNECTED);
        }
    }
}