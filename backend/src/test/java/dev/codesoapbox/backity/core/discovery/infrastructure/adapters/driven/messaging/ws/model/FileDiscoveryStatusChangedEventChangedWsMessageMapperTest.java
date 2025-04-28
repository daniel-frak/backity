package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryStatusChangedEventChangedWsMessageMapperTest {

    private static final FileDiscoveryStatusChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileDiscoveryStatusChangedWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        FileDiscoveryStatusChangedEvent event = TestFileDiscoveryEvent.statusChanged();

        FileDiscoveryStatusChangedWsEvent result = MAPPER.toWsEvent(event);

        FileDiscoveryStatusChangedWsEvent expectedResult = TestFileDiscoveryWsEvent.statusChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}