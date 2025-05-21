package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouldNotResolveUniqueFilePathExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new CouldNotResolveUniqueFilePathException("someGameTitle", "someFileName", 5);

        assertThat(exception.getMessage())
                .isEqualTo("Could not resolve unique file path for game 'someGameTitle'" +
                           " and file 'someFileName' after 5 attempts");
    }
}