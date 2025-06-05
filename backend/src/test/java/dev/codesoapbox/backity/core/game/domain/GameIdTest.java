package dev.codesoapbox.backity.core.game.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameIdTest {

    @Test
    void toStringShouldReturnValue() {
        String idString = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";
        var id = new GameId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }

    @Nested
    class Creation {
        @Test
        void shouldCreateFromString() {
            var result = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");

            var expectedValue = UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
            assertThat(result.value()).isEqualTo(expectedValue);
        }

        @Test
        void shouldCreateNewInstance() {
            GameId result = GameId.newInstance();

            assertThat(result.value()).isNotNull();
        }
    }

    @Nested
    class Comparable {

        @Test
        void shouldReturnZeroWhenComparingSameInstance() {
            var id1 = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");

            assertThat(id1).isEqualByComparingTo(id1);
        }

        @Test
        void shouldMaintainComparableAsymmetry() {
            var id1 = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
            var id2 = new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281");

            assertThat(id1.compareTo(id2)).isNotEqualTo(id2.compareTo(id1));
        }
    }
}