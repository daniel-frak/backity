package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        var exception = new FileCopyNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find FileCopy with id=6df888e8-90b9-4df5-a237-0cba422c0310";
        assertThat(result).isEqualTo(expectedResult);
    }
}