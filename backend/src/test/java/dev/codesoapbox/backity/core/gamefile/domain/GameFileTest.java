package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileTest {

    @Test
    void shouldCreateForGameAndFileSource() {
        Game game = TestGame.any();
        FileSource fileSource = TestFileSource.minimalGog();

        GameFile result = GameFile.createFor(game, fileSource);

        GameFile expectedResult = TestGameFile.gogBuilder()
                .id(result.getId())
                .gameId(game.getId())
                .fileSource(fileSource)
                .dateCreated(null)
                .dateModified(null)
                .build();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }
}