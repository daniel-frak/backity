package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileVersionTest {

    @Nested
    class Constructor {

        @Test
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowGivenNullValue() {
            assertThatThrownBy(() -> new FileVersion(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        void shouldThrowGivenBlankValue() {
            String blankValue = " ";

            assertThatThrownBy(() -> new FileVersion(blankValue))
                    .isInstanceOf(DomainValueIsEmptyException.class)
                    .hasMessageContaining("File version");
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnValue() {
            String value = "someValue";
            var fileVersion = new FileVersion(value);

            String result = fileVersion.toString();

            assertThat(result)
                    .isEqualTo(value);
        }
    }
}