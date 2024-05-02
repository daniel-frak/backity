package dev.codesoapbox.backity.core.gamefiledetails.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileDetailsIdTest {

    @Test
    void shouldCreateNewInstance() {
        GameFileDetailsId result = GameFileDetailsId.newInstance();

        assertThat(result.value()).isNotNull();
    }

    @Test
    void toStringShouldReturnValue() {
        String idString = "3b21cc23-54c6-48f3-914d-188b790128b4";
        var id = new GameFileDetailsId(UUID.fromString(idString));

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }
}