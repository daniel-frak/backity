package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryStartedWsEventMapperTest {

    private static final GameContentDiscoveryStartedWsEventMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryStartedWsEventMapper.class);

    @Test
    void shouldMapDomainToWs() {
        GameContentDiscoveryStartedEvent event = TestGameContentDiscoveryEvent.discoveryStarted();

        GameContentDiscoveryStartedWsEvent result = MAPPER.toWsEvent(event);

        GameContentDiscoveryStartedWsEvent expectedResult = TestGameContentDiscoveryWsEvent.discoveryStarted();
        assertThat(result).isEqualTo(expectedResult);
    }
}