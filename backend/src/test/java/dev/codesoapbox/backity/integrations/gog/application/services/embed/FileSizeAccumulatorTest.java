package dev.codesoapbox.backity.integrations.gog.application.services.embed;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileSizeAccumulatorTest {

    @Test
    void shouldAdd() {
        var accumulator = new FileSizeAccumulator();
        accumulator.add("5 KB");
        accumulator.add("10.5 MB");
        accumulator.add("20 GB");
        accumulator.add("1 TB");

        String result = accumulator.toString();

        assertEquals("1020010505000.0 bytes", result);
    }

    @Test
    void shouldGetInBytes() {
        var accumulator = new FileSizeAccumulator();
        accumulator.add("5 KB");

        Long result = accumulator.getInBytes();

        assertEquals(5000, result);
    }
}