package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.FileCopyId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilePathMustNotBeNullForSuccessfulFileCopyTest {

    @Test
    void shouldCreate() {
        var id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        var exception = new FilePathMustNotBeNullForSuccessfulFileCopy(id);

        String result = exception.getMessage();

        var expectedResult =
                "File path must not be null for successful file copy (id=6df888e8-90b9-4df5-a237-0cba422c0310)";
        assertThat(result).isEqualTo(expectedResult);
    }
}