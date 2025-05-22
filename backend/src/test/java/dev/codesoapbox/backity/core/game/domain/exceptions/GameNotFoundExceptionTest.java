package dev.codesoapbox.backity.core.game.domain.exceptions;

import dev.codesoapbox.backity.core.game.domain.GameId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        var exception = new GameNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find Game with id=5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";
        assertThat(result).isEqualTo(expectedResult);
    }
}