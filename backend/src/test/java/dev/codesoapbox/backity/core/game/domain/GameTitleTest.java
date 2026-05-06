package dev.codesoapbox.backity.core.game.domain;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainValueIsEmptyException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTitleTest {

    @Nested
    class Constructor {

        @Test
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowGivenNullValue() {
            assertThatThrownBy(() -> new GameTitle(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        void shouldThrowGivenBlankValue() {
            String blankValue = " ";

            assertThatThrownBy(() -> new GameTitle(blankValue))
                    .isInstanceOf(DomainValueIsEmptyException.class)
                    .hasMessageContaining("Game title");
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnValue() {
            String value = "someValue";
            var gameTitle = new GameTitle(value);

            String result = gameTitle.toString();

            assertThat(result)
                    .isEqualTo(value);
        }
    }
}