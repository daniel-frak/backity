package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvents;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveredWsEventMapperTest {

    private static final FileDiscoveredWsEventMapper MAPPER = Mappers.getMapper(FileDiscoveredWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        FileDiscoveredEvent event = TestFileDiscoveryEvents.discovered();

        FileDiscoveredWsEvent result = MAPPER.toWsEvent(event);

        FileDiscoveredWsEvent expectedResult = TestFileDiscoveryWsEvents.discovered();
        assertThat(result).isEqualTo(expectedResult);
    }
}