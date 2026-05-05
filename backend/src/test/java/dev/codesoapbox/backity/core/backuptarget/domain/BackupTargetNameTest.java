package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BackupTargetNameTest {

    @Test
    void constructorShouldThrowGivenBlankValue() {
        String blankValue = " ";

        assertThatThrownBy(() -> new BackupTargetName(blankValue))
                .isInstanceOf(DomainValueIsEmptyException.class)
                .hasMessageContaining("Backup target name");
    }

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var backupTargetName = new BackupTargetName(value);

        String result = backupTargetName.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}