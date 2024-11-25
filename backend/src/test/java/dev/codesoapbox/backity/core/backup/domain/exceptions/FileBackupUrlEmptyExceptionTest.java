package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
        var exception = new FileBackupUrlEmptyException(id);

        String result = exception.getMessage();

        var expectedResult = "Game file url was null or empty for GameFile with id: " +
                             "acde26d7-33c7-42ee-be16-bca91a604b48";
        assertThat(result).isEqualTo(expectedResult);
    }
}