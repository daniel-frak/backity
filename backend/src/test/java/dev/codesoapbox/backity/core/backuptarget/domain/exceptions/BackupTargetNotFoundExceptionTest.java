package dev.codesoapbox.backity.core.backuptarget.domain.exceptions;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BackupTargetNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");
        var exception = new BackupTargetNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find BackupTarget with id=3553a3c7-47a7-4f7a-8b47-75928bee37d0";
        assertThat(result).isEqualTo(expectedResult);
    }
}
