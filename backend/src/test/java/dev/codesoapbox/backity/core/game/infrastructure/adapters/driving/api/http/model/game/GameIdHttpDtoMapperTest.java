package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model.game;

import dev.codesoapbox.backity.core.game.domain.GameId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameIdHttpDtoMapperTest {

    private static final GameIdHttpDtoMapper MAPPER = new GameIdHttpDtoMapper();

    @Test
    void shouldMapToDto() {
        String idString = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";
        var id = new GameId(idString);

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