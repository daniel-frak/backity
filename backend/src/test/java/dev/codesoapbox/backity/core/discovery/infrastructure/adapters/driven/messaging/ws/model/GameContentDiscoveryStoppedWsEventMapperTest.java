package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryStoppedWsEventMapperTest {

    private static final GameContentDiscoveryStoppedWsEventMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryStoppedWsEventMapper.class);

    @Test
    void shouldMapToWsEvent() {
        GameContentDiscoveryStoppedEvent domain = domain();

        GameContentDiscoveryStoppedWsEvent result = MAPPER.toWsEvent(domain);

        GameContentDiscoveryStoppedWsEvent expectedResult = dto(domain);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GameContentDiscoveryStoppedEvent domain() {
        return TestGameContentDiscoveryEvent.discoveryStopped();
    }

    private GameContentDiscoveryStoppedWsEvent dto(GameContentDiscoveryStoppedEvent domain) {
        return new GameContentDiscoveryStoppedWsEvent(
                domain.gameProviderId().value(),
                new GameContentDiscoveryResultWsDto(
                        domain.discoveryResult().getStartedAt(),
                        domain.discoveryResult().getStoppedAt(),
                        GameContentDiscoveryOutcomeWsDto.SUCCESS,
                        domain.discoveryResult().getLastSuccessfulDiscoveryCompletedAt(),
                        domain.discoveryResult().getGamesDiscovered(),
                        domain.discoveryResult().getGameFilesDiscovered()
                )
        );
    }
}