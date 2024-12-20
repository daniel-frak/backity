package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStartedWsEventMapperTest {

    private static final FileBackupStartedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupStartedWsEventMapper.class);

    @Test
    void shouldMapBackupStartedToWsEvent() {
        FileBackupStartedEvent domain = TestFileBackupEvent.started();

        FileBackupStartedWsEvent result = MAPPER.toWsEvent(domain);

        FileBackupStartedWsEvent expectedResult = TestFileBackupWsEvent.started();
        assertThat(result).isEqualTo(expectedResult);
    }
}