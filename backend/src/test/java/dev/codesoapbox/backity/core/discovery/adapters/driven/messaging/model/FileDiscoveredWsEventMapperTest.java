package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveredWsEventMapperTest {

    private static final FileDiscoveredWsEventMapper MAPPER = Mappers.getMapper(FileDiscoveredWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        GameProviderFile gameProviderFile = discoveredGameFile().build().getGameProviderFile();

        FileDiscoveredWsEvent result = MAPPER.toWsEvent(gameProviderFile);

        var expectedResult = new FileDiscoveredWsEvent(
                gameProviderFile.originalGameTitle(),
                gameProviderFile.originalFileName(),
                gameProviderFile.fileTitle(),
                gameProviderFile.size()
        );
        assertThat(result).hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}