package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryStatusChangedEventChangedWsMessageMapperTest {

    private static final FileDiscoveryStatusChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileDiscoveryStatusChangedWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        var status = new FileDiscoveryStatusChangedEvent("Test Source", true);

        FileDiscoveryStatusChangedWsEvent result = MAPPER.toWsEvent(status);

        FileDiscoveryStatusChangedWsEvent expectedResult = new FileDiscoveryStatusChangedWsEvent(
                "Test Source", true);
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}