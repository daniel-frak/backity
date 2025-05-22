package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyNotBackedUpExceptionTest {

    @Test
    void shouldCreate() {
        var id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        var exception = new FileCopyNotBackedUpException(id);

        String result = exception.getMessage();

        var expectedResult = "FileCopy (id=6df888e8-90b9-4df5-a237-0cba422c0310) is not backed up";
        assertThat(result).isEqualTo(expectedResult);
    }
}