package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileVersionTest {

    @Test
    void constructorShouldThrowGivenBlankValue() {
        String blankValue = " ";

        assertThatThrownBy(() -> new FileVersion(blankValue))
                .isInstanceOf(DomainValueIsEmptyException.class)
                .hasMessageContaining("File version");
    }

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var fileVersion = new FileVersion(value);

        String result = fileVersion.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}