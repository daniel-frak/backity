package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStartedWsEventMapperTest {

    private static final FileBackupStartedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupStartedWsEventMapper.class);

    @Test
    void shouldMapBackupStartedToWsEvent() {
        FileBackupStartedEvent domain = TestFileBackupEvent.started();
        FileCopyWithContext fileCopyWithContext = new FileCopyWithContext(
                TestFileCopy.inProgressWithFilePath(),
                TestGameFile.gog(),
                TestGame.any()
        );

        FileBackupStartedWsEvent result = MAPPER.toWsEvent(domain, fileCopyWithContext);

        FileBackupStartedWsEvent expectedResult = TestFileBackupWsEvent.started();
        assertThat(result).isEqualTo(expectedResult);
    }
}