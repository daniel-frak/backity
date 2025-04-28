package dev.codesoapbox.backity.core.filemanagement.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.filemanagement.domain.exceptions.FileCouldNotBeDeletedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class LocalFileSystemIT {

    private LocalFileSystem localFileSystem;

    @BeforeEach
    void setUp() {
        localFileSystem = new LocalFileSystem();
    }

    @Test
    void shouldDeleteIfExists(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir + File.separator + "someFile";

        var existingFileCreated = new File(filePath).createNewFile();

        localFileSystem.deleteIfExists(filePath);

        var fileExists = Files.exists(Path.of(filePath));

        assertThat(existingFileCreated).isTrue();
        assertThat(fileExists).isFalse();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void deleteIfExistsShouldThrowGivenException(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir + File.separator + "someFile";
        var existingFile = new File(filePath);
        existingFile.createNewFile();
        tempDir.toFile().setWritable(false); // This will cause an IOException

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
    void shouldNotDeleteIfDoesNotExist(@TempDir Path tempDir) {
        String filePath = tempDir + File.separator + "someFile";

        assertThatCode(() -> localFileSystem.deleteIfExists(filePath))
                .doesNotThrowAnyException();
    }

    @Test
    void getOutputStreamShouldReturnValidOutputStream(@TempDir Path tempDir) throws IOException {
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
    void getOutputStreamShouldReturnValidOutputStreamWithSubfolders(@TempDir Path tempDir) throws IOException {
        var filePath = tempDir + File.separator + "subfolder" + File.separator + "someFile";
        var fileContent = "Test Data";

        try (OutputStream outputStream = localFileSystem.getOutputStream(filePath)) {
            outputStream.write(fileContent.getBytes());
        }

        assertThatDataWasWrittenToDisk(filePath, fileContent);
    }

    @Test
    void getOutputStreamShouldFailGivenFileAlreadyExists(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir + File.separator + "someFile";
        new File(filePath).createNewFile();

        assertThatThrownBy(() -> localFileSystem.getOutputStream(filePath))
                .isInstanceOf(FileAlreadyExistsException.class);
    }

    @Test
    void shouldGetSeparator() {
        String result = localFileSystem.getSeparator();

        assertThat(result).isEqualTo(File.separator);
    }

    @Test
    void getSizeInBytesShouldReturnCorrectSize(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir + File.separator + "someFile";
        String testData = "Test Data";
        try (var writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(testData);
        }

        var sizeInBytes = localFileSystem.getSizeInBytes(filePath);

        assertThat(sizeInBytes).isEqualTo(9);
    }

    @Test
    void getFileResourceShouldReturnFileResourceGivenFileExists(@TempDir Path tempDir) throws IOException {
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
    void getFileResourceShouldThrowGivenFileNotFound() {
        String filePath = "nonexistentfile.txt";

        assertThatThrownBy(() -> localFileSystem.getFileResource(filePath).close())
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("File not found: " + filePath);
    }

    @Test
    void getFileResourceShouldThrowGivenFileIsDirectory(@TempDir Path tempDir) throws IOException {
        Path tempDirectory = tempDir.resolve("tempDirectory");
        Files.createDirectory(tempDirectory);

        assertThatThrownBy(() -> localFileSystem.getFileResource(tempDirectory.toString()).close())
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("File not found: " + tempDirectory);
    }

    @Test
    void fileExistsShouldReturnTrueGivenFileExists(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir + File.separator + "someFile";
        new File(filePath).createNewFile();

        boolean result = localFileSystem.fileExists(filePath);

        assertThat(result).isTrue();
    }

    @Test
    void fileExistsShouldReturnTrueGivenFileDoesNotExist(@TempDir Path tempDir) {
        String nonExistentFilePath = tempDir + File.separator + "someFile";

        boolean result = localFileSystem.fileExists(nonExistentFilePath);

        assertThat(result).isFalse();
    }
}