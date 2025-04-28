package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveredWsEventMapperTest {

    private static final FileDiscoveredWsEventMapper MAPPER = Mappers.getMapper(FileDiscoveredWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        FileDiscoveredEvent event = TestFileDiscoveryEvent.discovered();

        FileDiscoveredWsEvent result = MAPPER.toWsEvent(event);

        FileDiscoveredWsEvent expectedResult = TestFileDiscoveryWsEvent.discovered();
        assertThat(result).isEqualTo(expectedResult);
    }
}