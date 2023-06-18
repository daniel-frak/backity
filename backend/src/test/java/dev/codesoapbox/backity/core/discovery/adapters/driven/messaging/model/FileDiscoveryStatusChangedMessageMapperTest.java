package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryStatusChangedMessageMapperTest {

    private static final FileDiscoveryStatusChangedMessageMapper MAPPER =
            Mappers.getMapper(FileDiscoveryStatusChangedMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        var status = new FileDiscoveryStatus("Test Source", true);

        FileDiscoveryStatusChangedMessage result = MAPPER.toMessage(status);

        FileDiscoveryStatusChangedMessage expectedResult = new FileDiscoveryStatusChangedMessage(
                "Test Source", true);
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}