package dev.codesoapbox.backity.core.filemanagement.domain;

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
public class FakeUnixFileManager implements FileManager {

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

    public FakeUnixFileManager(long availableSizeInBytes) {
        this.availableSizeInBytes = availableSizeInBytes;
    }

    @Override
    public boolean isEnoughFreeSpaceOnDisk(long sizeInBytes, String filePath) {
        pathsCheckedForSize.add(fixSeparatorChar(filePath));
        return availableSizeInBytes >= sizeInBytes;
    }

    private String fixSeparatorChar(String filePath) {
        return filePath
                .replace("/", "\\");
    }

    @Override
    public void createDirectories(String filePath) {
        directoriesCreated.add(fixSeparatorChar(filePath));
    }

    @Override
    public String renameFileAddingSuffixIfExists(String filePath, String fileName) {
        filesRenamed.put(filePath, fileName);
        Long sizeInBytes = bytesWrittenPerFilePath.get(filePath);
        bytesWrittenPerFilePath.put(fileName, sizeInBytes);
        return fileName;
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

    public boolean containsFile(String filePath) {
        return bytesWrittenPerFilePath.getOrDefault(filePath, 0L) > 0;
    }
}
