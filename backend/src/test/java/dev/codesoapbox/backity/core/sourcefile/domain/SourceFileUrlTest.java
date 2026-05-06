package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SourceFileUrlTest {

    @Nested
    class Constructor {

        @Test
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowGivenNullValue() {
            assertThatThrownBy(() -> new SourceFileUrl(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        void shouldThrowGivenBlankValue() {
            String blankValue = " ";

            assertThatThrownBy(() -> new SourceFileUrl(blankValue))
                    .isInstanceOf(DomainValueIsEmptyException.class)
                    .hasMessageContaining("Source file url");
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnValue() {
            String value = "someValue";
            var sourceFile = new SourceFileUrl(value);

            String result = sourceFile.toString();

            assertThat(result)
                    .isEqualTo(value);
        }
    }
}