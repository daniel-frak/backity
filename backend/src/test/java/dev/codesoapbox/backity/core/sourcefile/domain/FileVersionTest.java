package dev.codesoapbox.backity.core.sourcefile.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileVersionTest {

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var fileVersion = new FileVersion(value);

        String result = fileVersion.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}