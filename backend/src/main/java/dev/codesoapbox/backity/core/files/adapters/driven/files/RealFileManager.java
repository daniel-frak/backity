package dev.codesoapbox.backity.core.files.adapters.driven.files;

import dev.codesoapbox.backity.core.files.domain.downloading.services.FileManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

@Slf4j
public class RealFileManager implements FileManager {

    @Override
    public boolean isEnoughFreeSpaceOnDisk(long sizeInBytes, String filePath) {
        File file = new File(filePath);
        return file.getParentFile().getUsableSpace() >= sizeInBytes;
    }

    @Override
    public void createDirectories(String path) throws IOException {
        Files.createDirectories(FileSystems.getDefault().getPath(path));
    }

    @Override
    public String renameFile(String fullFilePath, String targetFileName) throws IOException {
        Path originalPath = Paths.get(fullFilePath);
        Path newPath = Paths.get(extractDirectory(fullFilePath) + File.separator + targetFileName);

        Files.move(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Renamed file {} to {}", originalPath, newPath);

        // @TODO Write test for return value
        return newPath.toAbsolutePath().toString();
    }

    @Override
    public OutputStream getOutputStream(String stringPath) throws IOException {
        Path path = FileSystems.getDefault().getPath(stringPath);
        return Files.newOutputStream(path);
    }

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf(File.separator));
    }
}
