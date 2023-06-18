package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.fullFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStatusChangedWsMessageMapperTest {

    private static final FileBackupStatusChangedMessageMapper MAPPER =
            Mappers.getMapper(FileBackupStatusChangedMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        GameFileDetails domain = fullFileDetails().build();

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