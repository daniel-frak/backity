package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileNotBackedUpExceptionTest {

    @Test
    void shouldCreate() {
        var id = new GameFileId("3b21cc23-54c6-48f3-914d-188b790128b4");
        var exception = new GameFileNotBackedUpException(id);

        String result = exception.getMessage();

        var expectedResult = "GameFile (id=3b21cc23-54c6-48f3-914d-188b790128b4) is not backed up";
        assertThat(result).isEqualTo(expectedResult);
    }
}