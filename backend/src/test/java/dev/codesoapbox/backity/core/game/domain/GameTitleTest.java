package dev.codesoapbox.backity.core.game.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTitleTest {

    @Test
    void constructorShouldThrowGivenBlankValue() {
        String blankValue = " ";

        assertThatThrownBy(() -> new GameTitle(blankValue))
                .isInstanceOf(DomainValueIsEmptyException.class)
                .hasMessageContaining("Game title");
    }

    @Test
    void toStringShouldReturnValue() {
        String value = "someValue";
        var gameTitle = new GameTitle(value);

        String result = gameTitle.toString();

        assertThat(result)
                .isEqualTo(value);
    }
}