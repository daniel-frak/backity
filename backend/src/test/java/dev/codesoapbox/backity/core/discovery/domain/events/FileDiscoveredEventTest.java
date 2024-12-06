package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveredEventTest {

    @Test
    void shouldCreateFrom() {
        GameFile gameFile = TestGameFile.discoveredGameFile().build();

        FileDiscoveredEvent result = FileDiscoveredEvent.from(gameFile);

        var expectedResult = new FileDiscoveredEvent(
                "someOriginalGameTitle",
                "someOriginalFileName",
                "someFileTitle",
                "5 KB"
        );
        assertThat(result).isEqualTo(expectedResult);
    }
}