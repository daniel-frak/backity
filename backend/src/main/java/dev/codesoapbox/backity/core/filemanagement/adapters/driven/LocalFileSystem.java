package dev.codesoapbox.backity.core.filemanagement.adapters.driven;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

@Slf4j
public class LocalFileSystem implements FileManager {

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
    public String renameFileAddingSuffixIfExists(String fullFilePath, String targetFileName) throws IOException {
        Path originalPath = Paths.get(fullFilePath);
        String fileName = getUniqueFileName(extractDirectory(fullFilePath), targetFileName);
        Path newPath = Paths.get(extractDirectory(fullFilePath) + File.separator + fileName);

        Files.move(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Renamed file {} to {}", originalPath, newPath);

        // @TODO Write test for return value
        return newPath.toAbsolutePath().toString();
    }

    private String getUniqueFileName(String directory, String fileName) {
        StringBuilder baseName = new StringBuilder(getBaseName(fileName));
        String targetBaseName = baseName.toString();
        String extension = "";
        if (baseName.length() < fileName.length()) {
            extension = fileName.substring(baseName.length());
        }
        int counter = 1;

        while (Files.exists(Paths.get(directory + getSeparator() + targetBaseName + extension))) {
            targetBaseName = baseName + "_" + counter;
            counter++;
        }

        return targetBaseName + extension;
    }

    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    @Override
    public OutputStream getOutputStream(String stringPath) throws IOException {
        Path path = FileSystems.getDefault().getPath(stringPath);
        return Files.newOutputStream(path);
    }

    @Override
    public void deleteIfExists(String path) throws IOException {
        Files.deleteIfExists(Path.of(path));
    }

    @Override
    public String getSeparator() {
        return File.separator;
    }

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf(File.separator));
    }
}
