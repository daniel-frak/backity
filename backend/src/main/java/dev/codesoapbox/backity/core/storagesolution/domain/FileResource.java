package dev.codesoapbox.backity.core.storagesolution.domain;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a physical file resource, as it exists on disk or in memory.
 */
public record FileResource(
        InputStream inputStream,
        long sizeInBytes,
        String fileName
) implements AutoCloseable {

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
