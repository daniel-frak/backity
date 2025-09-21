package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyReplicationProgressUpdatedWsEventMapperTest {

    private static final FileCopyReplicationProgressUpdatedWsEventMapper MAPPER =
            Mappers.getMapper(FileCopyReplicationProgressUpdatedWsEventMapper.class);

    @Test
    void shouldMapToToWsEvent() {
        FileCopyReplicationProgressChangedEvent domain = TestFileBackupEvent.progressChanged();

        FileCopyReplicationProgressUpdatedWsEvent result = MAPPER.toWsEvent(domain);

        FileCopyReplicationProgressUpdatedWsEvent expectedResult = TestFileBackupWsEvent.progressChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}