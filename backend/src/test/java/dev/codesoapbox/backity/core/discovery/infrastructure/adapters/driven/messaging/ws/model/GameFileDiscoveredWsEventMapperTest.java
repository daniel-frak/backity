package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameFileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileDiscoveredWsEventMapperTest {

    private static final GameFileDiscoveredWsEventMapper MAPPER = Mappers.getMapper(GameFileDiscoveredWsEventMapper.class);

    @Test
    void shouldMapToDomainToWs() {
        GameFileDiscoveredEvent domain = TestGameContentDiscoveryEvent.fileDiscovered();

        GameFileDiscoveredWsEvent result = MAPPER.toWsEvent(domain);

        GameFileDiscoveredWsEvent expectedResult = TestGameContentDiscoveryWsEvent.fileDiscovered();
        assertThat(result).isEqualTo(expectedResult);
    }
}