package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyStatusChangedWsEventMapperTest {

    private static final FileCopyStatusChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileCopyStatusChangedWsEventMapper.class);

    @Test
    void shouldMapBackupFinishedToWsEvent() {
        FileBackupFinishedEvent domain = TestFileBackupEvent.finishedUnverified();

        FileCopyStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        FileCopyStatusChangedWsEvent expectedResult = TestFileBackupWsEvent.finishedUnverified();
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldMapBackupFailedToWsEvent() {
        FileBackupFailedEvent domain = TestFileBackupEvent.failed();

        FileCopyStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        FileCopyStatusChangedWsEvent expectedResult = TestFileBackupWsEvent.failed();
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldMapBackupInProgressToWsEvent() {
        FileBackupStartedEvent domain = TestFileBackupEvent.started();

        FileCopyStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        FileCopyStatusChangedWsEvent expectedResult = TestFileBackupWsEvent.started();
        assertThat(result).isEqualTo(expectedResult);
    }
}