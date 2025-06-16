package dev.codesoapbox.backity.core.storagesolution.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    @Setter
    private Long customWrittenSizeInBytes = null;

    @Getter
    @Setter
    private long availableSizeInBytes = 0;

    @Setter
    private RuntimeException shouldThrowOnFileDeletion;

    private int outputStreamsCreated = 0;
    private int outputStreamsClosed = 0;

    public FakeUnixStorageSolution(long availableSizeInBytes) {
        this.availableSizeInBytes = availableSizeInBytes;
    }

    public boolean allOutputStreamsWereClosed() {
        return outputStreamsClosed == outputStreamsCreated;
    }

    private String fixSeparatorChar(String filePath) {
        return filePath
                .replace("\\", "/");
    }

    @Override
    public StorageSolutionId getId() {
        return ID;
    }

    @Override
    public ByteArrayOutputStream getOutputStream(String path) {
        outputStreamsCreated++;
        String unixPath = fixSeparatorChar(path); // In case we're not on a Unix system
        return new ByteArrayOutputStream() {
            @Override
            public synchronized void write(byte[] b, int off, int len) {
                super.write(b, off, len);
                bytesWrittenPerFilePath.put(
                        unixPath, bytesWrittenPerFilePath.getOrDefault(unixPath, 0L) + len);
            }

            @Override
            public synchronized void write(int b) {
                super.write(b);
                bytesWrittenPerFilePath.put(
                        unixPath, bytesWrittenPerFilePath.getOrDefault(unixPath, 0L) + 1);
            }

            @Override
            public void close() throws IOException {
                super.close();
                outputStreamsClosed++;
            }
        };

    }

    @Override
    public void deleteIfExists(String path) {
        path = fixSeparatorChar(path); // In case we're not on a Unix system
        if (shouldThrowOnFileDeletion != null) {
            throw shouldThrowOnFileDeletion;
        }
        bytesWrittenPerFilePath.remove(path);
        fileDeletesAttempted.add(path);
    }

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public long getSizeInBytes(String filePath) {
        filePath = fixSeparatorChar(filePath); // In case we're not on a Unix system
        if(customWrittenSizeInBytes != null) {
            return customWrittenSizeInBytes;
        }
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
        String unixPath = fixSeparatorChar(path); // In case we're not on a Unix system
        return pathsCheckedForSize.stream()
                .anyMatch(p -> p.contains(unixPath));
    }

    public boolean directoryWasCreated(String path) {
        String unixPath = fixSeparatorChar(path); // In case we're not on a Unix system
        return directoriesCreated.stream()
                .anyMatch(p -> p.contains(unixPath));
    }

    public boolean anyDirectoriesWereCreated() {
        return !directoriesCreated.isEmpty();
    }

    public boolean fileDeleteWasAttempted(String path) {
        path = fixSeparatorChar(path); // In case we're not on a Unix system
        return fileDeletesAttempted.contains(path);
    }

    public boolean anyFileDeleteWasAttempted() {
        return !fileDeletesAttempted.isEmpty();
    }

    public boolean fileWasRenamed(String filePath, String fileName) {
        filePath = fixSeparatorChar(filePath); // In case we're not on a Unix system
        return filesRenamed.getOrDefault(filePath, "").equals(fileName);
    }

    public void overrideDownloadedSizeFor(String filePath, Long sizeInBytes) {
        fakeBytesWrittenPerFilePath.put(filePath, sizeInBytes);
    }

    @Override
    public boolean fileExists(String filePath) {
        filePath = fixSeparatorChar(filePath); // In case we're not on a Unix system
        return bytesWrittenPerFilePath.getOrDefault(filePath, 0L) > 0;
    }

    @Override
    public StorageSolutionStatus getStatus() {
        return StorageSolutionStatus.CONNECTED;
    }

    public void createFile(String filePath) {
        filePath = fixSeparatorChar(filePath); // In case we're not on a Unix system
        bytesWrittenPerFilePath.put(filePath, 1L);
    }
}
