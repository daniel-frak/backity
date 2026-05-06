package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BackupTargetNameTest {

    @Nested
    class Constructor {

        @Test
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowGivenNullValue() {
            assertThatThrownBy(() -> new BackupTargetName(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        void shouldThrowGivenBlankValue() {
            String blankValue = " ";

            assertThatThrownBy(() -> new BackupTargetName(blankValue))
                    .isInstanceOf(DomainValueIsEmptyException.class)
                    .hasMessageContaining("Backup target name");
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnValue() {
            String value = "someValue";
            var backupTargetName = new BackupTargetName(value);

            String result = backupTargetName.toString();

            assertThat(result)
                    .isEqualTo(value);
        }
    }
}