package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgressRepository;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryStatusChangedEventRepositoryHandler
        implements DomainEventHandler<GameContentDiscoveryStatusChangedEvent> {

    private final GameContentDiscoveryProgressRepository gameContentDiscoveryProgressRepository;

    @Override
    public Class<GameContentDiscoveryStatusChangedEvent> getEventClass() {
        return GameContentDiscoveryStatusChangedEvent.class;
    }

    @Override
    public void handle(GameContentDiscoveryStatusChangedEvent event) {
        if (!event.isInProgress()) {
            gameContentDiscoveryProgressRepository.deleteByGameProviderId(event.gameProviderId());
        }
    }
}
