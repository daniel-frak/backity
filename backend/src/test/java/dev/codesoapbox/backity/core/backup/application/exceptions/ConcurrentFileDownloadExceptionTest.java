package dev.codesoapbox.backity.core.backup.application.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentFileDownloadExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new ConcurrentFileDownloadException("testPath");

        assertThat(exception)
                .hasMessage("File 'testPath' is currently being downloaded by another thread");
    }
}