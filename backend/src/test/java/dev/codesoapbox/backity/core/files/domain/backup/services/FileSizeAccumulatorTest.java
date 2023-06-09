package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.exceptions.UnrecognizedFileSizeUnitException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileSizeAccumulatorTest {

    @Test
    void shouldAdd() {
        var accumulator = new FileSizeAccumulator();
        accumulator.add("5 B");
        accumulator.add("5 KB");
        accumulator.add("10.5 MB");
        accumulator.add("20 GB");
        accumulator.add("1 TB");

        String result = accumulator.toString();

        assertEquals("1020010505005 bytes", result);
    }

    @Test
    void addShouldThrowExceptionIfFileSizeUnitUnrecognized() {
        var accumulator = new FileSizeAccumulator();
        assertThrows(UnrecognizedFileSizeUnitException.class, () -> accumulator.add("5 badunit"));
    }

    @Test
    void shouldGetInBytes() {
        var accumulator = new FileSizeAccumulator();
        accumulator.add("5 KB");

        Long result = accumulator.getInBytes();

        assertEquals(5000, result);
    }
}