package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.UnrecognizedFileSizeUnitException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        assertThat(result).isEqualTo("1020010505005 bytes");
    }

    @Test
    void addShouldThrowExceptionIfFileSizeUnitUnrecognized() {
        var accumulator = new FileSizeAccumulator();
        assertThatThrownBy(() -> accumulator.add("5 badunit"))
                .isInstanceOf(UnrecognizedFileSizeUnitException.class);
    }

    @Test
    void shouldGetInBytes() {
        var accumulator = new FileSizeAccumulator();
        accumulator.add("5 KB");

        Long result = accumulator.getInBytes();

        assertThat(result).isEqualTo(5000);
    }
}