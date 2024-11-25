package dev.codesoapbox.backity.core.gamefile.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileIdTest {

    @Test
    void shouldCreateFromString() {
        var result = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

        var expectedValue = UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48");
        assertThat(result.value()).isEqualTo(expectedValue);
    }

    @Test
    void shouldCreateNewInstance() {
        GameFileId result = GameFileId.newInstance();

        assertThat(result.value()).isNotNull();
    }

    @Test
    void toStringShouldReturnValue() {
        String idString = "3b21cc23-54c6-48f3-914d-188b790128b4";
        var id = new GameFileId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }
}