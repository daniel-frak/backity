package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.TestFileBackupEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDownloadProgressUpdatedWsEventMapperTest {

    private static final FileDownloadProgressUpdatedWsEventMapper MAPPER =
            Mappers.getMapper(FileDownloadProgressUpdatedWsEventMapper.class);

    @Test
    void shouldMapToToWsEvent() {
        FileDownloadProgressChangedEvent domain = TestFileBackupEvent.progressChanged();

        FileDownloadProgressUpdatedWsEvent result = MAPPER.toWsEvent(domain);

        FileDownloadProgressUpdatedWsEvent expectedResult = TestFileBackupWsEvent.progressChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}