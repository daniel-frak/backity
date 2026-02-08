package dev.codesoapbox.backity.core.sourcefile.domain;

import dev.codesoapbox.backity.core.discovery.domain.DiscoveredFile;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileTest {

    @Test
    void shouldCreateForGameAndDiscoveredFile() {
        Game game = TestGame.any();
        DiscoveredFile discoveredFile = TestDiscoveredFile.minimalGog();

        SourceFile result = SourceFile.createFor(game, discoveredFile);

        SourceFile expectedResult = TestSourceFile.gogBuilder()
                .id(result.getId())
                .gameId(game.getId())
                .dataFrom(discoveredFile)
                .dateCreated(null)
                .dateModified(null)
                .build();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }
}