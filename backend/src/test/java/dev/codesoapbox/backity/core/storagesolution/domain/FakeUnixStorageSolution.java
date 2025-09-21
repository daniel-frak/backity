package dev.codesoapbox.backity.core.storagesolution.domain;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class FakeUnixStorageSolution implements StorageSolution {

    public static final StorageSolutionId ID = new StorageSolutionId("FakeUnixStorageSolution");

    private final Map<String, Integer> openStreamCountByFilePath = new HashMap<>();
    private final Map<String, ByteArrayOutputStream> outputStreamsByPath = new HashMap<>();
    private final Map<String, Long> overriddenSizesByPath = new HashMap<>();

    @Setter
    private RuntimeException shouldThrowOnFileDeletion;

    @Setter
    private RuntimeException shouldThrowOnGetOutputStream;

    private static String asUnixPath(String path) {
        return path.replace('\\', '/');
    }

    public boolean allOutputStreamsWereClosed() {
        return openStreamCountByFilePath.values().stream()
                .allMatch(count -> count <= 0);
    }

    @Override
    public StorageSolutionId getId() {
        return ID;
    }

    @Override
    public ByteArrayOutputStream getOutputStream(String path) {
        if (shouldThrowOnGetOutputStream != null) {
            throw shouldThrowOnGetOutputStream;
        }
        String unixPath = asUnixPath(path);
        return newTrackedOutputStream(unixPath);
    }

    private ByteArrayOutputStream newTrackedOutputStream(String unixPath) {
        var outputStream = outputStreamsByPath.computeIfAbsent(unixPath, _ -> new ByteArrayOutputStream() {

            @Override
            public void close() {
                openStreamCountByFilePath.merge(unixPath, -1, Integer::sum);
            }
        });
        openStreamCountByFilePath.merge(unixPath, 1, Integer::sum);

        return outputStream;
    }

    @Override
    public void deleteIfExists(String path) {
        if (shouldThrowOnFileDeletion != null) {
            throw shouldThrowOnFileDeletion;
        }
        String unixPath = asUnixPath(path);
        outputStreamsByPath.remove(unixPath);
        openStreamCountByFilePath.remove(unixPath);
    }

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public long getSizeInBytes(String filePath) {
        String unixPath = asUnixPath(filePath);
        Long overridden = overriddenSizesByPath.get(unixPath);
        if (overridden != null) {
            return overridden;
        }
        ByteArrayOutputStream outputStream = getExistingOutputStream(unixPath);
        return outputStream.size();
    }

    private ByteArrayOutputStream getExistingOutputStream(String unixPath) {
        ByteArrayOutputStream outputStream = outputStreamsByPath.get(unixPath);
        if (outputStream == null) {
            throw new IllegalArgumentException("File '" + unixPath + "' does not exist");
        }
        return outputStream;
    }

    @Override
    public FileResource getFileResource(String filePath) {
        String unixPath = asUnixPath(filePath);
        ByteArrayOutputStream outputStream = getExistingOutputStream(unixPath);
        byte[] content = outputStream.toByteArray();

        return new FileResource(new ByteArrayInputStream(content), content.length, unixPath);
    }

    public void overrideWrittenSizeFor(String filePath, long sizeInBytes) {
        String unixPath = asUnixPath(filePath);
        overriddenSizesByPath.put(unixPath, sizeInBytes);
    }

    @Override
    public boolean fileExists(String filePath) {
        String unixPath = asUnixPath(filePath);
        return outputStreamsByPath.containsKey(unixPath);
    }

    @Override
    public StorageSolutionStatus getStatus() {
        return StorageSolutionStatus.CONNECTED;
    }

    @SneakyThrows
    public void createFile(String filePath) {
        String unixPath = asUnixPath(filePath);
        ByteArrayOutputStream stream = newTrackedOutputStream(unixPath);
        stream.write("Existing data".getBytes());
        stream.close();
    }

    public String getFileContent(String filePath) {
        String unixPath = asUnixPath(filePath);
        return getExistingOutputStream(unixPath).toString();
    }
}
