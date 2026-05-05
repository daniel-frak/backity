package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyFailureReasonTest {

    @Test
    void constructorShouldThrowGivenBlankValue() {
        String blankValue = " ";

        assertThatThrownBy(() -> new FileCopyFailureReason(blankValue))
                .isInstanceOf(DomainValueIsEmptyException.class)
                .hasMessageContaining("File copy failure reason");
    }

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var failedReason = new FileCopyFailureReason(value);

        String result = failedReason.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}