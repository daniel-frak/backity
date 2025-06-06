package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryStatusChangedWsEventMapperTest {

    private static final GameContentDiscoveryStatusChangedWsEventMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryStatusChangedWsEventMapper.class);

    @Test
    void shouldMapToDomainToWs() {
        GameContentDiscoveryStatusChangedEvent event = TestGameContentDiscoveryEvent.statusChangedToInProgress();

        GameContentDiscoveryStatusChangedWsEvent result = MAPPER.toWsEvent(event);

        GameContentDiscoveryStatusChangedWsEvent expectedResult = TestGameContentDiscoveryWsEvent.statusChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}