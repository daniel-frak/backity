package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupProgressUpdatedWsEventMapperTest {

    private static final FileBackupProgressUpdatedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupProgressUpdatedWsEventMapper.class);

    @Test
    void shouldMapToToWsEvent() {
        FileBackupProgressChangedEvent domain = TestFileBackupEvent.progressChanged();

        FileBackupProgressUpdatedWsEvent result = MAPPER.toWsEvent(domain);

        FileBackupProgressUpdatedWsEvent expectedResult = TestFileBackupWsEvent.progressChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}