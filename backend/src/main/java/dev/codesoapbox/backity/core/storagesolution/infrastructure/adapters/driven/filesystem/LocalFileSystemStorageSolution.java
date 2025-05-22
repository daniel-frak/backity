package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.FileCouldNotBeDeletedException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;

@Slf4j
public class LocalFileSystemStorageSolution implements StorageSolution {

    public static final StorageSolutionId ID = new StorageSolutionId("LOCAL_FILE_SYSTEM");

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public StorageSolutionId getId() {
        return ID;
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
