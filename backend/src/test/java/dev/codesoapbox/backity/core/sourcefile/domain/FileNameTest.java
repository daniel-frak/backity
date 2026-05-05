package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileNameTest {

    @Test
    void constructorShouldThrowGivenBlankValue() {
        String blankValue = " ";

        assertThatThrownBy(() -> new FileName(blankValue))
                .isInstanceOf(DomainValueIsEmptyException.class)
                .hasMessageContaining("File name");
    }

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var fileName = new FileName(value);

        String result = fileName.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}