package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedMessage;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedMessageMapper;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.fullFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStatusChangedMessageMapperTest {

    private static final FileBackupStatusChangedMessageMapper MAPPER =
            Mappers.getMapper(FileBackupStatusChangedMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        GameFileDetails domain = fullFileDetails().build();

        FileBackupStatusChangedMessage result = MAPPER.toMessage(domain);

        var expectedResult = new FileBackupStatusChangedMessage(
                domain.getId().value().toString(),
                domain.getBackupDetails().getStatus().toString(),
                domain.getBackupDetails().getFailedReason()
        );
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}