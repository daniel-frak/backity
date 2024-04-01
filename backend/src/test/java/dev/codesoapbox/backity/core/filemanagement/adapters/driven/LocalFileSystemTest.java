package dev.codesoapbox.backity.core.filemanagement.adapters.driven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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

        assertThat(new File(path).exists()).isTrue();
    }

    @Test
    void shouldRenameFile(@TempDir Path tempDir) throws IOException {
        String originalFilePath = tempDir + File.separator + "someFile";
        String newFileName = "newFileName";
        var fileCreated = new File(originalFilePath).createNewFile();

        String result = localFileSystem.renameFileAddingSuffixIfExists(originalFilePath, newFileName);

        assertThat(fileCreated).isTrue();
        assertThat(new File(tempDir + File.separator + newFileName).exists()).isTrue();
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
        assertThat(new File(tempDir + File.separator + newFileName + "_2").exists()).isTrue();
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
        assertThat(new File(tempDir + File.separator + newFileNameWithoutExtension + "_2" + extension)
                .exists()).isTrue();
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

    @Test
    void shouldNotDeleteIfDoesNotExist(@TempDir Path tempDir) {
        String filePath = tempDir + File.separator + "someFile";

        assertThatCode(() -> localFileSystem.deleteIfExists(filePath))
                .doesNotThrowAnyException();
    }
}