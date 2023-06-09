package dev.codesoapbox.backity.core.files.backup.domain.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupUrlEmptyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionBackupUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new FileBackupUrlEmptyException(1L);

        assertEquals("Game file url was null or empty for GameFileVersion with id: 1",
                exception.getMessage());
    }
}