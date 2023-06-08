package dev.codesoapbox.backity.core.files.adapters.driven.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RealFileManagerTest {

    private static final long SPACE_DIFFERENCE_BYTES = 9999999999L;
    private RealFileManager realFileManager;

    @BeforeEach
    void setUp() {
        realFileManager = new RealFileManager();
    }

    @Test
    void isEnoughFreeSpaceOnDiskShouldReturnTrueIfEnoughSpace(@TempDir Path tempDir) {
        var usableSpace = tempDir.toFile().getUsableSpace();
        var sizeInBytes = usableSpace - SPACE_DIFFERENCE_BYTES;

        var result = realFileManager.isEnoughFreeSpaceOnDisk(sizeInBytes,
                tempDir.toString());

        assertTrue(result);
    }

    @Test
    void isEnoughFreeSpaceOnDiskShouldReturnTrueIfExactlyEnoughSpace(@TempDir Path tempDir) {
        var usableSpace = tempDir.toFile().getUsableSpace();

        var result = realFileManager.isEnoughFreeSpaceOnDisk(usableSpace, tempDir.toString());

        assertTrue(result);
    }

    @Test
    void isEnoughFreeSpaceOnDiskShouldReturnFalseIfNotEnoughSpace(@TempDir Path tempDir) {
        var usableSpace = tempDir.toFile().getUsableSpace();
        var sizeInBytes = usableSpace + SPACE_DIFFERENCE_BYTES;

        var result = realFileManager.isEnoughFreeSpaceOnDisk(sizeInBytes,
                tempDir.toString());

        assertFalse(result);
    }

    @Test
    void shouldCreateDirectories(@TempDir Path tempDir) throws IOException {
        var path = tempDir + File.separator + "test1" + File.separator + "test2";

        realFileManager.createDirectories(path);

        assertTrue(new File(path).exists());
    }

    @Test
    void shouldRenameFile(@TempDir Path tempDir) throws IOException {
        String originalFilePath = tempDir + File.separator + "someFile";
        String newFileName = "newFileName";
        var fileCreated = new File(originalFilePath).createNewFile();

        realFileManager.renameFileAddingSuffixIfExists(originalFilePath, newFileName);

        assertTrue(fileCreated);
        assertTrue(new File(tempDir + File.separator + newFileName).exists());
    }

    @Test
    void shouldRenameFileAddingSuffixWithoutExtension(@TempDir Path tempDir) throws IOException {
        String originalFilePath = tempDir + File.separator + "someFile";
        String newFileName = "newFileName";
        String newFilePath = tempDir + File.separator + newFileName;
        var existingFile1Created = new File(newFilePath).createNewFile();
        var existingFile2Created = new File(newFilePath + "_1").createNewFile();
        var fileToRenameCreated = new File(originalFilePath).createNewFile();

        realFileManager.renameFileAddingSuffixIfExists(originalFilePath, newFileName);

        assertTrue(existingFile1Created);
        assertTrue(existingFile2Created);
        assertTrue(fileToRenameCreated);
        assertTrue(new File(tempDir + File.separator + newFileName + "_2").exists());
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

        realFileManager.renameFileAddingSuffixIfExists(originalFilePath, newFileNameWithExtension);

        assertTrue(existingFile1Created);
        assertTrue(existingFile2Created);
        assertTrue(fileToRenameCreated);
        assertTrue(new File(tempDir + File.separator + newFileNameWithoutExtension + "_2" + extension)
                .exists());
    }

    @Test
    void shouldDeleteIfExists(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir + File.separator + "someFile";

        var existingFileCreated = new File(filePath).createNewFile();

        realFileManager.deleteIfExists(filePath);

        var fileExists = Files.exists(Path.of(filePath));

        assertTrue(existingFileCreated);
        assertFalse(fileExists);
    }

    @Test
    void shouldNotDeleteIfDoesNotExist(@TempDir Path tempDir) {
        String filePath = tempDir + File.separator + "someFile";

        assertDoesNotThrow(() -> realFileManager.deleteIfExists(filePath));
    }
}