package dev.codesoapbox.backity.core.filemanagement.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.filemanagement.domain.FileSystem;
import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.filemanagement.domain.exceptions.FileCouldNotBeDeletedException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;

@Slf4j
public class LocalFileSystem implements FileSystem {

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public OutputStream getOutputStream(String stringPath) throws IOException {
        Path path = FileSystems.getDefault().getPath(stringPath);
        Files.createDirectories(path.getParent());
        return Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
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

    @Override
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
