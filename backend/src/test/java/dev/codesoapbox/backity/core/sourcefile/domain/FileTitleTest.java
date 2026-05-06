package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileTitleTest {

    @Nested
    class Constructor {

        @Test
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowGivenNullValue() {
            assertThatThrownBy(() -> new FileTitle(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        void shouldThrowGivenBlankValue() {
            String blankValue = " ";

            assertThatThrownBy(() -> new FileTitle(blankValue))
                    .isInstanceOf(DomainValueIsEmptyException.class)
                    .hasMessageContaining("File title");
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnValue() {
            String value = "someValue";
            var fileTitle = new FileTitle(value);

            String result = fileTitle.toString();

            assertThat(result)
                    .isEqualTo(value);
        }
    }
}