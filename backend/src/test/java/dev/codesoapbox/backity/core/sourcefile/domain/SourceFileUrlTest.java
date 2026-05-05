package dev.codesoapbox.backity.core.sourcefile.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileUrlTest {

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var sourceFile = new SourceFileUrl(value);

        String result = sourceFile.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}