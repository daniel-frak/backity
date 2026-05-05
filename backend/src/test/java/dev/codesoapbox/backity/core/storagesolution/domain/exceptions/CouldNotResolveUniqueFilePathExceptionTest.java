package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

import dev.codesoapbox.backity.core.game.domain.GameTitle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouldNotResolveUniqueFilePathExceptionTest {

    @Test
    void shouldGetMessage() {
        var gameTitle = new GameTitle("someGameTitle");
        var fileName = "someFileName";
        int attemptNumber = 5;
        var exception = new CouldNotResolveUniqueFilePathException(gameTitle, fileName, attemptNumber);

        assertThat(exception.getMessage())
                .isEqualTo("Could not resolve unique file path for game 'someGameTitle'" +
                           " and file 'someFileName' after 5 attempts");
    }
}