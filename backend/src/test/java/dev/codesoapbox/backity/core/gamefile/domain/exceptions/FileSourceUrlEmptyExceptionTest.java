package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileSourceUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new GameProviderId("TestGameProviderId");
        var exception = new FileSourceUrlEmptyException(id);

        String result = exception.getMessage();

        var expectedResult = "Url was empty for File Source with Game Provider id: TestGameProviderId";
        assertThat(result).isEqualTo(expectedResult);
    }
}