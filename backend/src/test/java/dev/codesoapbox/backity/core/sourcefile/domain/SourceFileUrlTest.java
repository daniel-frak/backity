package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SourceFileUrlTest {

    @Test
    void constructorShouldThrowGivenBlankValue() {
        String blankValue = " ";

        assertThatThrownBy(() -> new SourceFileUrl(blankValue))
                .isInstanceOf(DomainValueIsEmptyException.class)
                .hasMessageContaining("Source file url");
    }

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var sourceFile = new SourceFileUrl(value);

        String result = sourceFile.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}