package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.inmemory;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgressRepository;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryProgressChangedEventRepositoryHandler
        implements DomainEventHandler<GameContentDiscoveryProgressChangedEvent> {

    private final GameContentDiscoveryProgressRepository gameContentDiscoveryProgressRepository;

    @Override
    public Class<GameContentDiscoveryProgressChangedEvent> getEventClass() {
        return GameContentDiscoveryProgressChangedEvent.class;
    }

    @Override
    public void handle(GameContentDiscoveryProgressChangedEvent event) {
        GameContentDiscoveryProgress progress = toDiscoveryProgress(event);
        gameContentDiscoveryProgressRepository.save(progress);
    }

    private GameContentDiscoveryProgress toDiscoveryProgress(GameContentDiscoveryProgressChangedEvent event) {
        return new GameContentDiscoveryProgress(
                event.gameProviderId(),
                event.percentage(),
                event.timeLeft()
        );
    }
}
