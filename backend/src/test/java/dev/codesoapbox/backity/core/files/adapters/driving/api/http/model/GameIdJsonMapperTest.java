package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameIdJsonMapperTest {

    private final GameIdJsonMapper MAPPER = new GameIdJsonMapper();

    @Test
    void shouldMapToJson() {
        String idString = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";
        GameId id = new GameId(UUID.fromString(idString));

        String result = MAPPER.toJson(id);

        assertThat(result)
                .isEqualTo(idString);
    }

    @Test
    void shouldReturnNullGivenNull() {
        assertThat(MAPPER.toJson(null))
                .isNull();
    }
}