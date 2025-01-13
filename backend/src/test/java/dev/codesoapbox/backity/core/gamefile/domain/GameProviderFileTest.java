package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameProviderFileTest {

    @Test
    void shouldAssociateWith() {
        Game game = TestGame.any();
        GameProviderFile gameProviderFile = TestGameProviderFile.gog();

        GameFile result = gameProviderFile.associateWith(game);

        GameFile expectedResult = TestGameFile.discoveredBuilder()
                .id(result.getId())
                .gameId(game.getId())
                .gameProviderFile(gameProviderFile)
                .fileBackup(TestFileBackup.discovered())
                .dateCreated(null)
                .dateModified(null)
                .build();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }
}