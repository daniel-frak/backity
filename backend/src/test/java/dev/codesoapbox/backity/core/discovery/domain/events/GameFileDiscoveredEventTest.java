package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileDiscoveredEventTest {

    @Test
    void shouldCreateFrom() {
        GameFile gameFile = TestGameFile.gog();

        GameFileDiscoveredEvent result = GameFileDiscoveredEvent.from(gameFile);

        var expectedResult = new GameFileDiscoveredEvent(
                "Game 1",
                "game_1_installer.exe",
                "Game 1 (Installer)",
                new FileSize(5120L)
        );
        assertThat(result).isEqualTo(expectedResult);
    }
}