package dev.codesoapbox.backity.core.gamefiledetails.domain.exceptions;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileDetailsNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        GameFileDetailsId id = new GameFileDetailsId(UUID.fromString("3b21cc23-54c6-48f3-914d-188b790128b4"));
        var exception = new GameFileDetailsNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find GameFileDetails with id=3b21cc23-54c6-48f3-914d-188b790128b4";
        assertThat(result).isEqualTo(expectedResult);
    }
}