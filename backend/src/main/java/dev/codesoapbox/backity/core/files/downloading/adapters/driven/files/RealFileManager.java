package dev.codesoapbox.backity.core.files.downloading.adapters.driven.files;

import dev.codesoapbox.backity.core.files.downloading.domain.services.FileManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Slf4j
public class RealFileManager implements FileManager {

    @Override
    public boolean isEnoughFreeSpaceOnDisk(long sizeInBytes, String filePath) {
        File file = new File(filePath);
        return file.getUsableSpace() < sizeInBytes;
    }

    @Override
    public void createDirectories(String path) throws IOException {
        Files.createDirectories(FileSystems.getDefault().getPath(path));
    }

    @Override
    public void renameFile(String fullFilePath, String targetFileName) throws IOException {
        Path originalPath = Paths.get(fullFilePath);
        Path newPath = Paths.get(extractDirectory(fullFilePath) + File.separator + targetFileName);

        Files.move(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Renamed file {} to {}", originalPath, newPath);
    }

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf(File.separator));
    }
}
