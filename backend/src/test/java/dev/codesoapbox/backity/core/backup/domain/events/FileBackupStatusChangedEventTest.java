package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class FileBackupStatusChangedEventTest {

    @Test
    void shouldCreateFromGameFile() {
        GameFile gameFile = discoveredGameFile().build();

        FileBackupStatusChangedEvent result = FileBackupStatusChangedEvent.from(gameFile);

        var expectedResult = new FileBackupStatusChangedEvent(
                new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"),
                FileBackupStatus.DISCOVERED,
                null
        );
        assertThat(result).isEqualTo(expectedResult);
    }
}