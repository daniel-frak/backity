package dev.codesoapbox.backity.core.sourcefile.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new SourceFileUrlEmptyException();

        String result = exception.getMessage();

        var expectedResult = "Source file url is empty";
        assertThat(result).isEqualTo(expectedResult);
    }
}