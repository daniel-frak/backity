package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveredWsEventMapperTest {

    private static final FileDiscoveredWsEventMapper MAPPER = Mappers.getMapper(FileDiscoveredWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        FileDiscoveredEvent event = TestGameContentDiscoveryEvent.fileDiscovered();

        FileDiscoveredWsEvent result = MAPPER.toWsEvent(event);

        FileDiscoveredWsEvent expectedResult = TestGameContentDiscoveryWsEvent.fileDiscovered();
        assertThat(result).isEqualTo(expectedResult);
    }
}