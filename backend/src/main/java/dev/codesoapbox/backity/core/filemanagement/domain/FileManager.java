package dev.codesoapbox.backity.core.filemanagement.domain;

import java.io.IOException;
import java.io.OutputStream;

public interface FileManager {

    boolean isEnoughFreeSpaceOnDisk(long sizeInBytes, String filePath);

    void createDirectories(String filePath) throws IOException;

    /**
     * Renames the file, but adds a suffix to it if another file already exists with the new name.
     * <p>
     * E.g., if {@code fileName} is {@code setup.exe} and another file with that name already exists, the file will be
     * renamed to {@code setup_1.exe}, instead.
     *
     * @param filePath the full path of the file to be renamed (including the file itself)
     * @param fileName the target name for the file (without the preceding path)
     * @return the full path to the file after renaming
     * @throws IOException if an error occurs while renaming the file
     */
    String renameFileAddingSuffixIfExists(String filePath, String fileName) throws IOException;

    OutputStream getOutputStream(String path) throws IOException;

    void deleteIfExists(String path) throws IOException;

    String getSeparator();

    long getSizeInBytes(String filePath);
}
