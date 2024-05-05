package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.fullFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStatusChangedWsMessageMapperTest {

    private static final FileBackupStatusChangedMessageMapper MAPPER =
            Mappers.getMapper(FileBackupStatusChangedMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        FileDetails domain = fullFileDetails().build();

        FileBackupStatusChangedWsMessage result = MAPPER.toMessage(domain);

        var expectedResult = new FileBackupStatusChangedWsMessage(
                domain.getId().value().toString(),
                domain.getBackupDetails().getStatus().toString(),
                domain.getBackupDetails().getFailedReason()
        );
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}