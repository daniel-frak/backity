package dev.codesoapbox.backity.core.backup.application.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileDownloadWasCancelledExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new FileDownloadWasCancelledException("test");

        String result = exception.getMessage();

        assertThat(result).isEqualTo("File download was cancelled for 'test'");
    }
}