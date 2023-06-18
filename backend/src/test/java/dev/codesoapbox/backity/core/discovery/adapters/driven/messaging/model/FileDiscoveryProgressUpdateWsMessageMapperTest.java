package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryProgressUpdateWsMessageMapperTest {

    private static final FileDiscoveryProgressUpdateMessageMapper MAPPER =
            Mappers.getMapper(FileDiscoveryProgressUpdateMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        var progress = new FileDiscoveryProgress("Test Source", 50, 10);

        FileDiscoveryProgressUpdateWsMessage result = MAPPER.toMessage(progress);

        FileDiscoveryProgressUpdateWsMessage expectedResult = new FileDiscoveryProgressUpdateWsMessage(
                "Test Source", 50, 10);
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}