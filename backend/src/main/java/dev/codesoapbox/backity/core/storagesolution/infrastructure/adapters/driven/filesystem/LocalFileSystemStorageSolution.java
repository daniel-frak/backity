package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.*;
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
    public OutputStream getOutputStream(FilePath filePath) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath.toString());
        Files.createDirectories(path.getParent());
        return Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
    }

    @Override
    public void deleteIfExists(FilePath filePath) {
        try {
            Files.deleteIfExists(Path.of(filePath.toString()));
        } catch (RuntimeException | IOException e) {
            throw new FileCouldNotBeDeletedException(filePath, e);
        }
    }

    @Override
    public long getSizeInBytes(FilePath filePath) {
        var file = new File(filePath.toString());
        return file.length();
    }

    @Override
    public FileResource getFileResource(FilePath filePath) throws FileNotFoundException {
        File file = new File(filePath.toString());

        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        var inputStream = new FileInputStream(file);
        long sizeInBytes = file.length();

        return new FileResource(inputStream, sizeInBytes, file.getName());
    }

    @Override
    public boolean fileExists(FilePath filePath) {
        return Files.exists(Paths.get(filePath.toString()));
    }

    @Override
    public StorageSolutionStatus getStatus() {
        return StorageSolutionStatus.CONNECTED;
    }
}
