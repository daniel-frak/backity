package dev.codesoapbox.backity.core.backup.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnrecognizedFileSizeUnitExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new UnrecognizedFileSizeUnitException("test");

        String result = exception.getMessage();

        var expectedResult = "File size unit unrecognized: test";
        assertThat(result).isEqualTo(expectedResult);
    }
}