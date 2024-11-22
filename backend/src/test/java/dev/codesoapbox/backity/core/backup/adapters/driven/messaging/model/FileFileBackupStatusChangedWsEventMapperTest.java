package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.fullGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStatusChangedWsEventMapperTest {

    private static final FileBackupStatusChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);

    @Test
    void shouldMapToWsEvent() {
        GameFile domain = fullGameFile().build();

        FileBackupStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        var expectedResult = new FileBackupStatusChangedWsEvent(
                domain.getId().value().toString(),
                domain.getFileBackup().getStatus().toString(),
                domain.getFileBackup().getFailedReason()
        );
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}