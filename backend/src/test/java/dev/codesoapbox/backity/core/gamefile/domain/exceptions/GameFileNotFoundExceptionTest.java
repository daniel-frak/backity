package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new GameFileId("3b21cc23-54c6-48f3-914d-188b790128b4");
        var exception = new GameFileNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find GameFile with id=3b21cc23-54c6-48f3-914d-188b790128b4";
        assertThat(result).isEqualTo(expectedResult);
    }
}