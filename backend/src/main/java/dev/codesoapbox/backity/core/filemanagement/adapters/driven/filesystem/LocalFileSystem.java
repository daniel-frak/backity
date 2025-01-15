package dev.codesoapbox.backity.core.filemanagement.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.filemanagement.domain.exceptions.FileCouldNotBeDeletedException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
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

        return newPath.toAbsolutePath().toString();
    }

    private String getUniqueFileName(String directory, String fileName) {
        String baseName = getBaseName(fileName);
        String targetBaseName = baseName;
        String extension = "";
        extension = fileName.substring(baseName.length());
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
    public void deleteIfExists(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (RuntimeException | IOException e) {
            throw new FileCouldNotBeDeletedException(path, e);
        }
    }

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public long getSizeInBytes(String filePath) {
        var file = new File(filePath);
        return file.length();
    }

    @Override
    public FileResource getFileResource(String filePath) throws FileNotFoundException {
        File file = new File(filePath);

        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        var inputStream = new FileInputStream(file);
        long sizeInBytes = file.length();

        return new FileResource(inputStream, sizeInBytes, file.getName());
    }

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf(File.separator));
    }
}
