package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvents;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupProgressUpdatedWsEventMapperTest {

    private static final FileBackupProgressUpdatedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupProgressUpdatedWsEventMapper.class);

    @Test
    void shouldMapToToWsEvent() {
        FileBackupProgressChangedEvent domain = TestFileBackupEvents.progressChanged();

        FileBackupProgressUpdatedWsEvent result = MAPPER.toWsEvent(domain);

        FileBackupProgressUpdatedWsEvent expectedResult = TestFileBackupWsEvents.progressChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}