package dev.codesoapbox.backity.core.backuptarget.domain.exceptions;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BackupTargetIsInUseExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new BackupTargetId("4a39cb1a-47da-45c3-b6cd-88dfc4c2499c");
        var exception = new BackupTargetIsInUseException(id);

        var result = exception.getMessage();

        var expectedResult = "BackupTarget with id=4a39cb1a-47da-45c3-b6cd-88dfc4c2499c is in use";
        assertEquals(expectedResult, result);
    }
}