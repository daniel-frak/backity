package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameIdHttpDtoMapperTest {

    private final GameIdHttpDtoMapper MAPPER = new GameIdHttpDtoMapper();

    @Test
    void shouldMapToDto() {
        String idString = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";
        GameId id = new GameId(UUID.fromString(idString));

        String result = MAPPER.toDto(id);

        assertThat(result)
                .isEqualTo(idString);
    }

    @Test
    void shouldReturnNullGivenNull() {
        assertThat(MAPPER.toDto(null))
                .isNull();
    }
}