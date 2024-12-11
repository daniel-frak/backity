package dev.codesoapbox.backity.core.backup.application.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotEnoughFreeSpaceExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new NotEnoughFreeSpaceException("somePath");

        String result = exception.getMessage();

        var expectedResult = "Not enough space left to save: somePath";
        assertThat(result).isEqualTo(expectedResult);
    }
}