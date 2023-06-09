package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotEnoughFreeSpaceExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new NotEnoughFreeSpaceException("somePath");

        assertEquals("Not enough space left to save: somePath", exception.getMessage());
    }
}