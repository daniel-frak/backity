package dev.codesoapbox.backity.core.storagesolution.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilePathTest {

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var filePath = new FilePath(value);

        String result = filePath.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}