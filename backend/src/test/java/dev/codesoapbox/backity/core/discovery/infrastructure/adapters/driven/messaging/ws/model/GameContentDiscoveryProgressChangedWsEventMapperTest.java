package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryProgressChangedWsEventMapperTest {

    private static final GameContentDiscoveryProgressChangedWsEventMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryProgressChangedWsEventMapper.class);

    @Test
    void shouldMapToDomainToWs() {
        GameContentDiscoveryProgressChangedEvent domain = TestGameContentDiscoveryEvent.progressChanged();

        GameContentDiscoveryProgressChangedWsEvent result = MAPPER.toWsEvent(domain);

        GameContentDiscoveryProgressChangedWsEvent expectedResult = TestGameContentDiscoveryWsEvent.progressChanged();
        assertThat(result).isEqualTo(expectedResult);
    }
}