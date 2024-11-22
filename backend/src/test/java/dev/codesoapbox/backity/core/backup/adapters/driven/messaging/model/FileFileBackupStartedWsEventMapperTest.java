package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.fullGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class FileFileBackupStartedWsEventMapperTest {

    private static final FileBackupStartedWsEventMapper MAPPER = Mappers.getMapper(FileBackupStartedWsEventMapper.class);

    @Test
    void shouldMapToWsEvent() {
        GameFile domain = fullGameFile().build();

        FileBackupStartedWsEvent result = MAPPER.toWsEvent(domain);

        var expectedResult = new FileBackupStartedWsEvent(
                domain.getId().value().toString(),
                domain.getGameProviderFile().originalGameTitle(),
                domain.getGameProviderFile().fileTitle(),
                domain.getGameProviderFile().version(),
                domain.getGameProviderFile().originalFileName(),
                domain.getGameProviderFile().size(),
                domain.getFileBackup().getFilePath()
        );

        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}