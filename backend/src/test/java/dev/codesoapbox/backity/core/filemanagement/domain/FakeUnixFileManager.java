package dev.codesoapbox.backity.core.filemanagement.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class FakeUnixFileManager implements FileManager {

    @Getter
    @Setter
    private long availableSizeInBytes = 0;

    private Set<String> pathsCheckedForSize = new HashSet<>();
    private Set<String> directoriesCreated = new HashSet<>();
    private Map<String, String> filesRenamed = new HashMap<>();
    private Set<String> filesDeleted = new HashSet<>();

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
        return filePath;
    }

    @Override
    public ByteArrayOutputStream getOutputStream(String path) {
        return new ByteArrayOutputStream();
    }

    @Override
    public void deleteIfExists(String path) {
        filesDeleted.add(path);
    }

    @Override
    public String getSeparator() {
        return File.separator;
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

    public boolean fileWasDeleted(String path) {
        return filesDeleted.contains(path);
    }
}
