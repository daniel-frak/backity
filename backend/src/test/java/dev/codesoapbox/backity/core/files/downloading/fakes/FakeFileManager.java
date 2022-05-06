package dev.codesoapbox.backity.core.files.downloading.fakes;

import dev.codesoapbox.backity.core.files.downloading.domain.services.FileManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class FakeFileManager implements FileManager {

    @Getter
    @Setter
    private long availableSizeInBytes = 0;

    private Set<String> pathsCheckedForSize = new HashSet<>();
    private Set<String> directoriesCreated = new HashSet<>();
    private Map<String, String> filesRenamed = new HashMap<>();

    public FakeFileManager(long availableSizeInBytes) {
        this.availableSizeInBytes = availableSizeInBytes;
    }

    @Override
    public boolean isEnoughFreeSpaceOnDisk(long sizeInBytes, String filePath) {
        pathsCheckedForSize.add(filePath);
        return availableSizeInBytes >= sizeInBytes;
    }

    @Override
    public void createDirectories(String filePath) {
        directoriesCreated.add(filePath);
    }

    @Override
    public void renameFile(String filePath, String fileName) {
        filesRenamed.put(filePath, fileName);
    }

    public boolean freeSpaceWasCheckedFor(String path) {
        return pathsCheckedForSize.stream()
                .anyMatch(p -> p.contains(path));
    }

    public boolean directoryWasCreated(String path) {
        return directoriesCreated.stream()
                .anyMatch(p -> p.contains(path));
    }
}
