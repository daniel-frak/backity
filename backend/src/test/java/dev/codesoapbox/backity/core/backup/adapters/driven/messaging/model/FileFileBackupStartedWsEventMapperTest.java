package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.fullFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStartedWsEventMapperTest {

    private static final FileBackupStartedWsEventMapper MAPPER = Mappers.getMapper(FileBackupStartedWsEventMapper.class);

    @Test
    void shouldMapToWsEvent() {
        FileDetails domain = fullFileDetails().build();

        FileBackupStartedWsEvent result = MAPPER.toWsEvent(domain);

        var expectedResult = new FileBackupStartedWsEvent(
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