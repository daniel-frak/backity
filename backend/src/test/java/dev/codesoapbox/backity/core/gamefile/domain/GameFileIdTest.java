package dev.codesoapbox.backity.core.gamefile.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileIdTest {

    @Test
    void shouldCreateNewInstance() {
        GameFileId result = GameFileId.newInstance();

        assertThat(result.value()).isNotNull();
    }

    @Test
    void toStringShouldReturnValue() {
        String idString = "3b21cc23-54c6-48f3-914d-188b790128b4";
        var id = new GameFileId(UUID.fromString(idString));

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }
}