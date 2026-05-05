package dev.codesoapbox.backity.core.sourcefile.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileTitleTest {

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var fileTitle = new FileTitle(value);

        String result = fileTitle.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}