package dev.codesoapbox.backity.core.discovery.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryExceptionTest {

    @Test
    void shouldGetMessage() {
        var testMessage = "test message";
        var exception = new FileDiscoveryException(testMessage);
        assertThat(exception).hasMessageContaining(testMessage);
    }
}