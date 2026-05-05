package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileTitleTest {

    @Test
    void constructorShouldThrowGivenBlankValue() {
        String blankValue = " ";

        assertThatThrownBy(() -> new FileTitle(blankValue))
                .isInstanceOf(DomainValueIsEmptyException.class)
                .hasMessageContaining("File title");
    }

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var fileTitle = new FileTitle(value);

        String result = fileTitle.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}