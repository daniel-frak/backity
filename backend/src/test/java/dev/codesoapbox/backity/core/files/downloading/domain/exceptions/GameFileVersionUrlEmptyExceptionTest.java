package dev.codesoapbox.backity.core.files.downloading.domain.exceptions;

import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.GameFileDownloadUrlEmptyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new GameFileDownloadUrlEmptyException(1L);

        assertEquals("Game file url was null or empty for GameFileVersion with id: 1",
                exception.getMessage());
    }
}