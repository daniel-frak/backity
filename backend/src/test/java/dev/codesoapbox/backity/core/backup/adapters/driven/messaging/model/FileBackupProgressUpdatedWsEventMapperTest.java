package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupProgressUpdatedWsEventMapperTest {

    private static final FileBackupProgressUpdatedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupProgressUpdatedWsEventMapper.class);

    @Test
    void shouldMapToToWsEvent() {
        var domain = new FileBackupProgress(75, 999);

        var result = MAPPER.toWsEvent(domain);

        var expectedResult = new FileBackupProgressUpdatedWsEvent(75, 999);
        assertThat(result).isEqualTo(expectedResult);
    }
}