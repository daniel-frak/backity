package dev.codesoapbox.backity.core.filemanagement.domain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public interface FileManager {

    OutputStream getOutputStream(String path) throws IOException;

    void deleteIfExists(String path);

    String getSeparator();

    long getSizeInBytes(String filePath);

    FileResource getFileResource(String filePath) throws FileNotFoundException;

    boolean fileExists(String filePath);
}
