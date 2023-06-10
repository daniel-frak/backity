package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackupUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        GameFileDetailsId id = new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));
        var exception = new FileBackupUrlEmptyException(id);

        assertEquals("Game file url was null or empty for GameFileDetails with id: " +
                "acde26d7-33c7-42ee-be16-bca91a604b48", exception.getMessage());
    }
}