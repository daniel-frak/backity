package dev.codesoapbox.backity.core.files.domain.downloading.services;

import java.io.IOException;
import java.io.OutputStream;

public interface FileManager {

    boolean isEnoughFreeSpaceOnDisk(long sizeInBytes, String filePath);

    void createDirectories(String filePath) throws IOException;

    String renameFile(String filePath, String fileName) throws IOException;

    OutputStream getOutputStream(String path) throws IOException;
}
