package dev.codesoapbox.backity.core.game.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameTitleTest {

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var gameTitle = new GameTitle(value);

        String result = gameTitle.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}