package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryProgressChangedEventUpdateWsMessageMapperTest {

    private static final FileDiscoveryProgressChangedWsEventMapper MAPPER =
            Mappers.getMapper(FileDiscoveryProgressChangedWsEventMapper.class);

    @Test
    void shouldMapToMessage() {
        FileDiscoveryProgressChangedEvent event = TestFileDiscoveryEvent.progressChanged();

        FileDiscoveryProgressChangedWsEvent result = MAPPER.toWsEvent(event);

        FileDiscoveryProgressChangedWsEvent expectedResult = TestFileDiscoveryWsEvent.progressChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}