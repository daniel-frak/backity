package dev.codesoapbox.backity.core.storagesolution.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class FakeUnixStorageSolution implements StorageSolution {

    public static final StorageSolutionId ID = new StorageSolutionId("FakeUnixStorageSolution");

    private final Set<String> pathsCheckedForSize = new HashSet<>();
    private final Set<String> directoriesCreated = new HashSet<>();
    private final Map<String, String> filesRenamed = new HashMap<>();
    private final Set<String> fileDeletesAttempted = new HashSet<>();
    private final Map<String, Long> bytesWrittenPerFilePath = new HashMap<>();
    private final Map<String, Long> fakeBytesWrittenPerFilePath = new HashMap<>();

    @Getter
    @Setter
    private long availableSizeInBytes = 0;

    @Setter
    private RuntimeException shouldThrowOnFileDeletion;

    public FakeUnixStorageSolution(long availableSizeInBytes) {
        this.availableSizeInBytes = availableSizeInBytes;
    }

    private String fixSeparatorChar(String filePath) {
        return filePath
                .replace("/", "\\");
    }

    @Override
    public StorageSolutionId getId() {
        return ID;
    }

    @Override
    public ByteArrayOutputStream getOutputStream(String path) {
        return new ByteArrayOutputStream() {
            @Override
            public synchronized void write(byte[] b, int off, int len) {
                super.write(b, off, len);
                bytesWrittenPerFilePath.put(path, bytesWrittenPerFilePath.getOrDefault(path, 0L) + len);
            }

            @Override
            public synchronized void write(int b) {
                super.write(b);
                bytesWrittenPerFilePath.put(path, bytesWrittenPerFilePath.getOrDefault(path, 0L) + 1);
            }
        };

    }

    @Override
    public void deleteIfExists(String path) {
        if (shouldThrowOnFileDeletion != null) {
            throw shouldThrowOnFileDeletion;
        }
        fileDeletesAttempted.add(path);
    }

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public long getSizeInBytes(String filePath) {
        if (fakeBytesWrittenPerFilePath.containsKey(filePath)) {
            return fakeBytesWrittenPerFilePath.get(filePath);
        }

        if (!bytesWrittenPerFilePath.containsKey(filePath)) {
            throw new IllegalArgumentException(String.format("File '%s' does not exist", filePath));
        }

        return bytesWrittenPerFilePath.get(filePath);
    }

    @Override
    public FileResource getFileResource(String filePath) throws FileNotFoundException {
        throw new FileNotFoundException("Not implemented");
    }

    public boolean freeSpaceWasCheckedFor(String path) {
        return pathsCheckedForSize.stream()
                .anyMatch(p -> p.contains(fixSeparatorChar(path)));
    }

    public boolean directoryWasCreated(String path) {
        return directoriesCreated.stream()
                .anyMatch(p -> p.contains(fixSeparatorChar(path)));
    }

    public boolean anyDirectoriesWereCreated() {
        return !directoriesCreated.isEmpty();
    }

    public boolean fileDeleteWasAttempted(String path) {
        return fileDeletesAttempted.contains(path);
    }

    public boolean anyFileDeleteWasAttempted() {
        return !fileDeletesAttempted.isEmpty();
    }

    public boolean fileWasRenamed(String filePath, String fileName) {
        return filesRenamed.getOrDefault(filePath, "").equals(fileName);
    }

    public void overrideDownloadedSizeFor(String filePath, Long sizeInBytes) {
        fakeBytesWrittenPerFilePath.put(filePath, sizeInBytes);
    }

    @Override
    public boolean fileExists(String filePath) {
        return bytesWrittenPerFilePath.getOrDefault(filePath, 0L) > 0;
    }

    @Override
    public StorageSolutionStatus getStatus() {
        return StorageSolutionStatus.CONNECTED;
    }

    public void createFile(String filePath) {
        bytesWrittenPerFilePath.put(filePath, 1L);
    }
}
