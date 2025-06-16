package dev.codesoapbox.backity.core.backup.application.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileDownloadFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var expectedResult = "someMessage";
        var exception = new FileDownloadFailedException(expectedResult);

        String result = exception.getMessage();

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldGetMessageWithCause() {
        var expectedMessage = "someMessage";
        var expectedCause = new RuntimeException("someThrowable");
        var exception = new FileDownloadFailedException(expectedMessage, expectedCause);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getCause()).isEqualTo(expectedCause);
    }
}