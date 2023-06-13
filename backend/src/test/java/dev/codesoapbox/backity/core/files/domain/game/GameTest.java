package dev.codesoapbox.backity.core.files.domain.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameTest {

    @Test
    void shouldCreateNew() {
        String gameTitle = "Test game title";
        Game result = Game.createNew(gameTitle);

        assertThat(result.getTitle()).isEqualTo(gameTitle);
    }
}