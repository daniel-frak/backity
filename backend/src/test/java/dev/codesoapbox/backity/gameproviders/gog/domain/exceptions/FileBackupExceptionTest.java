package dev.codesoapbox.backity.gameproviders.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupExceptionTest {

    @Test
    void shouldGetMessage() {
        var expectedResult = "someMessage";
        var exception = new FileBackupException(expectedResult);

        String result = exception.getMessage();

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldGetMessageWithCause() {
        var expectedMessage = "someMessage";
        var expectedCause = new RuntimeException("someThrowable");
        var exception = new FileBackupException(expectedMessage, expectedCause);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        assertThat(exception.getCause()).isEqualTo(expectedCause);
    }
}