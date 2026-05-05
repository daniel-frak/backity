package dev.codesoapbox.backity.core.storagesolution.domain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public interface StorageSolution {

    StorageSolutionId getId();

    OutputStream getOutputStream(FilePath filePath) throws IOException;

    void deleteIfExists(FilePath filePath);

    String getSeparator();

    long getSizeInBytes(FilePath filePath);

    FileResource getFileResource(FilePath filePath) throws FileNotFoundException;

    boolean fileExists(FilePath filePath);

    StorageSolutionStatus getStatus();
}
