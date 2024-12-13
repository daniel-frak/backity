package dev.codesoapbox.backity.shared.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainInvariantViolationExceptionTest {

    @Test
    void shouldCreateDefault() {
        var exception = new DomainInvariantViolationException();
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void shouldCreateWithMessage() {
        String message = "Some message";
        var exception = new DomainInvariantViolationException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void shouldCreateWithMessageAndCause() {
        String message = "Some message";
        Throwable cause = new RuntimeException();
        var exception = new DomainInvariantViolationException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldCreateWithCause() {
        Throwable cause = new RuntimeException();
        var exception = new DomainInvariantViolationException(cause);

        assertThat(exception.getCause()).isEqualTo(cause);
    }
}