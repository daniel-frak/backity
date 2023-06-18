package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.fullFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStartedWsMessageMapperTest {

    private static final FileBackupStartedMessageMapper MAPPER = Mappers.getMapper(FileBackupStartedMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        GameFileDetails domain = fullFileDetails().build();

        FileBackupStartedWsMessage result = MAPPER.toMessage(domain);

        var expectedResult = new FileBackupStartedWsMessage(
                domain.getId().value().toString(),
                domain.getSourceFileDetails().originalGameTitle(),
                domain.getSourceFileDetails().fileTitle(),
                domain.getSourceFileDetails().version(),
                domain.getSourceFileDetails().originalFileName(),
                domain.getSourceFileDetails().size(),
                domain.getBackupDetails().getFilePath()
        );

        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}