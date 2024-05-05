package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new FileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));
        var exception = new FileBackupUrlEmptyException(id);

        String result = exception.getMessage();

        var expectedResult = "Game file url was null or empty for FileDetails with id: " +
                "acde26d7-33c7-42ee-be16-bca91a604b48";
        assertThat(result).isEqualTo(expectedResult);
    }
}