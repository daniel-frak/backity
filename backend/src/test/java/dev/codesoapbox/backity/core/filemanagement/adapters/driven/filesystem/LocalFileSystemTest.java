package dev.codesoapbox.backity.core.filemanagement.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.filemanagement.domain.exceptions.FileCouldNotBeDeletedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class LocalFileSystemTest {

    private static final long SPACE_DIFFERENCE_BYTES = 9999999999L;
    private LocalFileSystem localFileSystem;

    @BeforeEach
    void setUp() {
        localFileSystem = new LocalFileSystem();
    }

    @Test
    void isEnoughFreeSpaceOnDiskShouldReturnTrueIfEnoughSpace(@TempDir Path tempDir) {
        var usableSpace = tempDir.toFile().getUsableSpace();
        var sizeInBytes = usableSpace - SPACE_DIFFERENCE_BYTES;

        var result = localFileSystem.isEnoughFreeSpaceOnDisk(sizeInBytes,
                tempDir.toString());

        assertThat(result).isTrue();
    }

    @Test
    void isEnoughFreeSpaceOnDiskShouldReturnTrueIfExactlyEnoughSpace(@TempDir Path tempDir) {
        var usableSpace = tempDir.toFile().getUsableSpace();

        var result = localFileSystem.isEnoughFreeSpaceOnDisk(usableSpace, tempDir.toString());

        assertThat(result).isTrue();
    }

    @Test
    void isEnoughFreeSpaceOnDiskShouldReturnFalseIfNotEnoughSpace(@TempDir Path tempDir) {
        var usableSpace = tempDir.toFile().getUsableSpace();
        var sizeInBytes = usableSpace + SPACE_DIFFERENCE_BYTES;

        var result = localFileSystem.isEnoughFreeSpaceOnDisk(sizeInBytes,
                tempDir.toString());

        assertThat(result).isFalse();
    }

    @Test
    void shouldCreateDirectories(@TempDir Path tempDir) throws IOException {
        var path = tempDir + File.separator + "test1" + File.separator + "test2";

        localFileSystem.createDirectories(path);

        assertThat(new File(path)).exists();
    }

    @Test
    void shouldRenameFile(@TempDir Path tempDir) throws IOException {
        String originalFilePath = tempDir + File.separator + "someFile";
        String newFileName = "newFileName";
        var fileCreated = new File(originalFilePath).createNewFile();

        String result = localFileSystem.renameFileAddingSuffixIfExists(originalFilePath, newFileName);

        assertThat(fileCreated).isTrue();
        assertThat(new File(tempDir + File.separator + newFileName)).exists();
        assertThat(result).isEqualTo(tempDir + File.separator + newFileName);
    }

    @Test
    void shouldRenameFileAddingSuffixWithoutExtension(@TempDir Path tempDir) throws IOException {
        String originalFilePath = tempDir + File.separator + "someFile";
        String newFileName = "newFileName";
        String newFilePath = tempDir + File.separator + newFileName;
        var existingFile1Created = new File(newFilePath).createNewFile();
        var existingFile2Created = new File(newFilePath + "_1").createNewFile();
        var fileToRenameCreated = new File(originalFilePath).createNewFile();

        localFileSystem.renameFileAddingSuffixIfExists(originalFilePath, newFileName);

        assertThat(existingFile1Created).isTrue();
        assertThat(existingFile2Created).isTrue();
        assertThat(fileToRenameCreated).isTrue();
        assertThat(new File(tempDir + File.separator + newFileName + "_2")).exists();
    }

    @Test
    void shouldRenameFileAddingSuffixWithExtension(@TempDir Path tempDir) throws IOException {
        String originalFilePath = tempDir + File.separator + "someFile";
        String newFileNameWithoutExtension = "newFileName";
        String extension = ".exe";
        String newFileNameWithExtension = newFileNameWithoutExtension + extension;
        String newFilePathWithoutExtension = tempDir + File.separator + newFileNameWithoutExtension;
        var existingFile1Created = new File(newFilePathWithoutExtension + extension).createNewFile();
        var existingFile2Created = new File(newFilePathWithoutExtension + "_1" + extension).createNewFile();
        var fileToRenameCreated = new File(originalFilePath).createNewFile();

        localFileSystem.renameFileAddingSuffixIfExists(originalFilePath, newFileNameWithExtension);

        assertThat(existingFile1Created).isTrue();
        assertThat(existingFile2Created).isTrue();
        assertThat(fileToRenameCreated).isTrue();
        assertThat(new File(tempDir + File.separator + newFileNameWithoutExtension + "_2" + extension))
                .exists();
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
        String filePath = tempDir + File.separator + "someFile";
        String testData = "Test Data";

        try (OutputStream outputStream = localFileSystem.getOutputStream(filePath)) {
            outputStream.write(testData.getBytes());
        }

        assertThatDataWasWrittenToDisk(filePath, testData);
    }

    private static void assertThatDataWasWrittenToDisk(String filePath, String testData) throws IOException {
        byte[] readBytes = Files.readAllBytes(Path.of(filePath));
        String readData = new String(readBytes);
        assertThat(readData).isEqualTo(testData);
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

        FileResource fileResource = localFileSystem.getFileResource(tempFile.toString());

        assertThat(fileResource).isNotNull();
        assertThat(fileResource.sizeInBytes()).isPositive();
        assertThat(fileResource.fileName()).isEqualTo(fileName);
        assertThatCode(fileResource::close)
                .doesNotThrowAnyException();
    }

    @Test
    void getFileResourceShouldThrowGivenFileNotFound() {
        String filePath = "nonexistentfile.txt";

        assertThatThrownBy(() -> localFileSystem.getFileResource(filePath))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("File not found: " + filePath);
    }

    @Test
    void getFileResourceShouldThrowGivenFileIsDirectory(@TempDir Path tempDir) throws IOException {
        Path tempDirectory = tempDir.resolve("tempDirectory");
        Files.createDirectory(tempDirectory);

        assertThatThrownBy(() -> localFileSystem.getFileResource(tempDirectory.toString()))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("File not found: " + tempDirectory);
    }
}