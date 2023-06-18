package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupProgressUpdateWsMessageMapperTest {

    private static final FileBackupProgressUpdateMessageMapper MAPPER =
            Mappers.getMapper(FileBackupProgressUpdateMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        var progress = new FileBackupProgress(50, 10);

        FileBackupProgressUpdateWsMessage result = MAPPER.toMessage(progress);

        FileBackupProgressUpdateWsMessage expectedResult = new FileBackupProgressUpdateWsMessage(
                50, 10);
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}