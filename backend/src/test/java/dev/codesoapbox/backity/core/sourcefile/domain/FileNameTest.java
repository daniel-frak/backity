package dev.codesoapbox.backity.core.sourcefile.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileNameTest {

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var fileName = new FileName(value);

        String result = fileName.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}