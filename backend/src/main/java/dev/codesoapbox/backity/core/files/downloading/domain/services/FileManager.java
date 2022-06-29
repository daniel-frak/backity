package dev.codesoapbox.backity.core.files.downloading.domain.services;

import java.io.IOException;
import java.io.OutputStream;

public interface FileManager {

    boolean isEnoughFreeSpaceOnDisk(long sizeInBytes, String filePath);

    void createDirectories(String filePath) throws IOException;

    void renameFile(String filePath, String fileName) throws IOException;

    OutputStream getOutputStream(String path) throws IOException;
}
