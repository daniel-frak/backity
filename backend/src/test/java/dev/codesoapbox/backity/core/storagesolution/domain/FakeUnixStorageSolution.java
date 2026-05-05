package dev.codesoapbox.backity.core.storagesolution.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class FakeUnixStorageSolution implements StorageSolution {

    public static final StorageSolutionId DEFAULT_ID = new StorageSolutionId("FakeUnixStorageSolution");

    private final Map<FilePath, Integer> openStreamCountByFilePath = new HashMap<>();
    private final Map<FilePath, ByteArrayOutputStream> outputStreamsByFilePath = new HashMap<>();
    private final Map<FilePath, Long> overriddenSizesByFilePath = new HashMap<>();

    @Setter
    private RuntimeException shouldThrowOnFileDeletion;

    @Setter
    private RuntimeException shouldThrowOnGetOutputStream;

    @Getter
    @Setter
    private StorageSolutionId id = DEFAULT_ID;

    private static FilePath asUnixPath(FilePath path) {
        String value = path.toString().replace('\\', '/');
        return new FilePath(value);
    }

    public boolean allOutputStreamsWereClosed() {
        return openStreamCountByFilePath.values().stream()
                .allMatch(count -> count <= 0);
    }

    @Override
    public ByteArrayOutputStream getOutputStream(FilePath filePath) {
        if (shouldThrowOnGetOutputStream != null) {
            throw shouldThrowOnGetOutputStream;
        }
        FilePath unixPath = asUnixPath(filePath);
        return newTrackedOutputStream(unixPath);
    }

    private ByteArrayOutputStream newTrackedOutputStream(FilePath unixPath) {
        var outputStream = outputStreamsByFilePath.computeIfAbsent(unixPath, _ -> new ByteArrayOutputStream() {

            @Override
            public void close() {
                openStreamCountByFilePath.merge(unixPath, -1, Integer::sum);
            }
        });
        openStreamCountByFilePath.merge(unixPath, 1, Integer::sum);

        return outputStream;
    }

    @Override
    public void deleteIfExists(FilePath filePath) {
        if (shouldThrowOnFileDeletion != null) {
            throw shouldThrowOnFileDeletion;
        }
        FilePath unixPath = asUnixPath(filePath);
        outputStreamsByFilePath.remove(unixPath);
        openStreamCountByFilePath.remove(unixPath);
    }

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public long getSizeInBytes(FilePath filePath) {
        FilePath unixPath = asUnixPath(filePath);
        Long overridden = overriddenSizesByFilePath.get(unixPath);
        if (overridden != null) {
            return overridden;
        }
        ByteArrayOutputStream outputStream = getExistingOutputStream(unixPath);
        return outputStream.size();
    }

    private ByteArrayOutputStream getExistingOutputStream(FilePath unixPath) {
        ByteArrayOutputStream outputStream = outputStreamsByFilePath.get(unixPath);
        if (outputStream == null) {
            throw new IllegalArgumentException("File '" + unixPath + "' does not exist");
        }
        return outputStream;
    }

    @Override
    public FileResource getFileResource(FilePath filePath) {
        FilePath unixPath = asUnixPath(filePath);
        ByteArrayOutputStream outputStream = getExistingOutputStream(unixPath);
        byte[] content = outputStream.toByteArray();

        return new FileResource(new ByteArrayInputStream(content), content.length, unixPath.toString());
    }

    public void overrideWrittenSizeFor(FilePath filePath, long sizeInBytes) {
        FilePath unixPath = asUnixPath(filePath);
        overriddenSizesByFilePath.put(unixPath, sizeInBytes);
    }

    @Override
    public boolean fileExists(FilePath filePath) {
        FilePath unixPath = asUnixPath(filePath);
        return outputStreamsByFilePath.containsKey(unixPath);
    }

    @Override
    public StorageSolutionStatus getStatus() {
        return StorageSolutionStatus.CONNECTED;
    }

    @SneakyThrows
    public void createFile(FilePath filePath) {
        FilePath unixPath = asUnixPath(filePath);
        ByteArrayOutputStream stream = newTrackedOutputStream(unixPath);
        stream.write("Existing data".getBytes());
        stream.close();
    }

    public String getFileContent(FilePath filePath) {
        FilePath unixPath = asUnixPath(filePath);
        return getExistingOutputStream(unixPath).toString();
    }
}
