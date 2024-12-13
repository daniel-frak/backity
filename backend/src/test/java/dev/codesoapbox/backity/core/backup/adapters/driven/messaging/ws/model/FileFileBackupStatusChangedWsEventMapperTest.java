package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvents;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStatusChangedWsEventMapperTest {

    private static final FileBackupStatusChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);

    @Test
    void shouldMapBackupFinishedToWsEvent() {
        FileBackupFinishedEvent domain = TestFileBackupEvents.finished();

        FileBackupStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        FileBackupStatusChangedWsEvent expectedResult = TestFileBackupWsEvents.finished();
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldMapBackupFailedToWsEvent() {
        FileBackupFailedEvent domain = TestFileBackupEvents.failed();

        FileBackupStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        FileBackupStatusChangedWsEvent expectedResult = TestFileBackupWsEvents.failed();
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldMapBackupInProgressToWsEvent() {
        FileBackupStartedEvent domain = TestFileBackupEvents.started();

        FileBackupStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        FileBackupStatusChangedWsEvent expectedResult = TestFileBackupWsEvents.startedAsStatusChange();
        assertThat(result).isEqualTo(expectedResult);
    }
}