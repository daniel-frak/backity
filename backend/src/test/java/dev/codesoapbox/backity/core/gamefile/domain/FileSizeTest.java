package dev.codesoapbox.backity.core.gamefile.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileSizeTest {

    @ParameterizedTest
    @CsvSource({
            "500 B,500",
            "500B,500",
            "500B ,500",
            " 500 B,500"
    })
    void fromStringShouldHandleValidInputs(String input, long expectedBytes) {
        FileSize fileSize = FileSize.fromString(input);

        assertThat(fileSize.getBytes()).isEqualTo(expectedBytes);
    }

    @SuppressWarnings("java:S4144")
    @ParameterizedTest
    @CsvSource({
            "1.5 KB,1536",
            "1.5 MB,1_572_864",
            "1.5 GB,1_610_612_736",
            "1.5 TB,1_649_267_441_664",
            "1.5 PB,1_688_849_860_263_936"
    })
    void fromStringShouldConvertToBytes(String input, long expectedBytes) {
        FileSize fileSize = FileSize.fromString(input);

        assertThat(fileSize.getBytes()).isEqualTo(expectedBytes);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'',Size string cannot be null or empty",
            "' ',Size string cannot be null or empty",
            "null,Size string cannot be null or empty",
            "5XB,Unknown unit in size string: XB",
            "MB,Invalid size string",
            "5,Invalid size string"
    }, nullValues = "null")
    void fromStringShouldHandleInvalidInputs(String input, String expectedMessage) {
        assertThatThrownBy(() -> FileSize.fromString(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);
    }

    @ParameterizedTest
    @CsvSource({
            "500,500 B",
            "1024,1 KB",
            "1_048_576,1 MB",
            "1_073_741_824,1 GB",
            "1_099_511_627_776,1 TB",
            "1_500,1.5 KB",
            "1_572_864,1.5 MB",
            "1_610_612_736,1.5 GB",
            "1_650_796_032_000,1.5 TB",
            "1_688_849_860_263_936,1.5 PB"
    })
    void toStringShouldReturnCorrectValue(long input, String expected) {
        String result = new FileSize(input).toString();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldAdd() {
        var fileSize1 = new FileSize(5L);
        var fileSize2 = new FileSize(10L);

        FileSize result = fileSize1.add(fileSize2);

        var expectedResult = new FileSize(15L);
        assertThat(result).isEqualTo(expectedResult);
    }
}