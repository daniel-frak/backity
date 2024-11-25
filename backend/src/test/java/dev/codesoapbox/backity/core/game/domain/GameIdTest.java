package dev.codesoapbox.backity.core.game.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameIdTest {

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

    @Test
    void toStringShouldReturnValue() {
        String idString = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";
        var id = new GameId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }
}