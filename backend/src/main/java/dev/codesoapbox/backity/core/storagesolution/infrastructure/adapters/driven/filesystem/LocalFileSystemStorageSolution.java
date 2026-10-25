package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.*;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.FileCouldNotBeDeletedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
public class LocalFileSystemStorageSolution implements StorageSolution {

    public static final StorageSolutionId ID = new StorageSolutionId("LOCAL_FILE_SYSTEM");

    private static final String PARENT_DIRECTORY_MARKER = "..";

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public StorageSolutionId getId() {
        return ID;
    }

    @SuppressFBWarnings(
            value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "path will always have a parent"
    )
    @Override
    public OutputStream getOutputStream(FilePath filePath) throws IOException {
        Path path = toSafePath(filePath);
        Files.createDirectories(path.getParent());

        return Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
    }

    private Path toSafePath(FilePath filePath) {
        Path path = FileSystems.getDefault().getPath(filePath.toString());

        for (Path segment : path) {
            if (segment.toString().equals(PARENT_DIRECTORY_MARKER)) {
                throw new IllegalArgumentException("Path must not contain '..': " + filePath);
            }
        }

        return path;
    }

    @Override
    public void deleteIfExists(FilePath filePath) {
        try {
            Path path = toSafePath(filePath);
            Files.deleteIfExists(path);
        } catch (RuntimeException | IOException exception) {
            throw new FileCouldNotBeDeletedException(filePath, exception);
        }
    }

    @Override
    public long getSizeInBytes(FilePath filePath) {
        Path path = toSafePath(filePath);

        try {
            return Files.size(path);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not read file size: " + filePath, exception);
        }
    }

    @SuppressFBWarnings(
            value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Files.isRegularFile(path) already guarantees that path.getFileName() is not null"
    )
    @Override
    public FileResource getFileResource(FilePath filePath) throws FileNotFoundException {
        Path path = toSafePath(filePath);

        if (!Files.isRegularFile(path)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try {
            long sizeInBytes = Files.size(path);
            Path fileName = path.getFileName();
            InputStream inputStream = Files.newInputStream(path);

            return new FileResource(inputStream, sizeInBytes, fileName.toString());
        } catch (IOException exception) {
            var fileNotFoundException = new FileNotFoundException("Could not read file: " + filePath);
            fileNotFoundException.initCause(exception);
            throw fileNotFoundException;
        }
    }

    @Override
    public boolean fileExists(FilePath filePath) {
        return Files.exists(toSafePath(filePath));
    }

    @Override
    public StorageSolutionStatus getStatus() {
        return StorageSolutionStatus.CONNECTED;
    }
}
