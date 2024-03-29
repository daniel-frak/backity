package dev.codesoapbox.backity.core.backup.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnrecognizedFileSizeUnitExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new UnrecognizedFileSizeUnitException("test");
        assertEquals("File size unit unrecognized: test", exception.getMessage());
    }
}