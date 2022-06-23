package dev.codesoapbox.backity.core.files.downloading.adapters.driven.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void createDirectories(@TempDir Path tempDir) throws IOException {
        var path = tempDir + File.separator + "test1" + File.separator + "test2";

        realFileManager.createDirectories(path);

        assertTrue(new File(path).exists());
    }

    @Test
    void renameFile(@TempDir Path tempDir) throws IOException {
        String originalFilePath = tempDir + File.separator + "someFile";
        String newFileName = "newFileName";
        var fileCreated = new File(originalFilePath).createNewFile();

        realFileManager.renameFile(originalFilePath, newFileName);

        assertTrue(fileCreated);
        assertTrue(new File(tempDir + File.separator + newFileName).exists());
    }
}