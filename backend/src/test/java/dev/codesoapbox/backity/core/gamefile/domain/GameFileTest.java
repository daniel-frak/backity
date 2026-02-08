package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileTest {

    @Test
    void shouldCreateForGameAndDiscoveredFile() {
        Game game = TestGame.any();
        DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();

        GameFile result = GameFile.createFor(game, discoveredFile);

        GameFile expectedResult = TestGameFile.gogBuilder()
                .id(result.getId())
                .gameId(game.getId())
                .dataFrom(discoveredFile)
                .dateCreated(null)
                .dateModified(null)
                .build();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }
}