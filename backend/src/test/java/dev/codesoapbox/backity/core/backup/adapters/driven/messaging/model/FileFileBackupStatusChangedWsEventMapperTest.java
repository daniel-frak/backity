package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.fullFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStatusChangedWsEventMapperTest {

    private static final FileBackupStatusChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileBackupStatusChangedWsEventMapper.class);

    @Test
    void shouldMapToWsEvent() {
        FileDetails domain = fullFileDetails().build();

        FileBackupStatusChangedWsEvent result = MAPPER.toWsEvent(domain);

        var expectedResult = new FileBackupStatusChangedWsEvent(
                domain.getId().value().toString(),
                domain.getBackupDetails().getStatus().toString(),
                domain.getBackupDetails().getFailedReason()
        );
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}