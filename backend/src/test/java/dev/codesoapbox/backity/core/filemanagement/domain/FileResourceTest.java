package dev.codesoapbox.backity.core.filemanagement.domain;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FileResourceTest {

    @Test
    void shouldClose() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        var fileResource = new FileResource(inputStream, 128, "someFileName");

        fileResource.close();

        verify(inputStream).close();
    }
}