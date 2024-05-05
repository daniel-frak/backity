package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileBackupFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        FileDetails fileDetails = discoveredFileDetails().build();

        var exception = new FileBackupFailedException(fileDetails, null);

        assertThat(exception.getMessage())
                .isEqualTo("Could not back up game file acde26d7-33c7-42ee-be16-bca91a604b48");
    }

    @Test
    void shouldGetCause() {
        FileDetails fileDetails = discoveredFileDetails().build();
        var cause = new RuntimeException("test");

        var exception = new FileBackupFailedException(fileDetails, cause);

        assertThat(exception.getCause()).isSameAs(cause);
    }
}