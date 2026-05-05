package dev.codesoapbox.backity.shared.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainValueIsEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new DomainValueIsEmptyException("SomeProperty");

        String result = exception.getMessage();

        var expectedResult = "SomeProperty is empty";
        assertThat(result).isEqualTo(expectedResult);
    }
}