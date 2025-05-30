package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidFileCopyStatusTransitionExceptionTest {

    @Test
    void shouldGetMessage() {
        var fileCopyId = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        FileCopyStatus statusFrom = FileCopyStatus.ENQUEUED;
        FileCopyStatus statusTo = FileCopyStatus.STORED_INTEGRITY_UNKNOWN;
        var exception = new InvalidFileCopyStatusTransitionException(fileCopyId, statusFrom, statusTo);

        String result = exception.getMessage();

        String expectedResult = "Invalid FileCopy status transition: ENQUEUED -> STORED_INTEGRITY_UNKNOWN" +
                                " (id=6df888e8-90b9-4df5-a237-0cba422c0310)";
        assertThat(result).isEqualTo(expectedResult);
    }
}