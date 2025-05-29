package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FilePathMustNotBeNullForStoredFileCopyTest {

    @Test
    void shouldCreate() {
        var id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        var exception = new FilePathMustNotBeNullForStoredFileCopy(id);

        String result = exception.getMessage();

        var expectedResult =
                "File path must not be null for stored file copy (id=6df888e8-90b9-4df5-a237-0cba422c0310)";
        assertThat(result).isEqualTo(expectedResult);
    }
}