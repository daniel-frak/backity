package dev.codesoapbox.backity.core.backup.application.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentFileWriteExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new ConcurrentFileWriteException("testPath");

        assertThat(exception)
                .hasMessage("File 'testPath' is currently being written to by another thread");
    }
}