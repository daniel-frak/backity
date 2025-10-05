package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.time.LocalDateTime;
import java.util.Map;

// @TODO Test
public class GameContentDiscoveryStoppedEventOutboxJpaSerializer
        implements DomainEventOutboxJpaSerializer<GameContentDiscoveryStoppedEvent> {

    @Override
    public Map<String, Object> serialize(GameContentDiscoveryStoppedEvent event) {
        return Map.of(
                "gameProviderId", event.gameProviderId().value(),
                "startedAt", event.discoveryResult().getStartedAt().toString(),
                "stoppedAt", event.discoveryResult().getStartedAt().toString(),
                "discoveryOutcome", event.discoveryResult().getDiscoveryOutcome().name(),
                "lastSuccessfulDiscoveryCompletedAt",
                event.discoveryResult().getLastSuccessfulDiscoveryCompletedAt().toString(),
                "gamesDiscovered", event.discoveryResult().getGamesDiscovered(),
                "gameFilesDiscovered", event.discoveryResult().getGameFilesDiscovered()
        );
    }

    @Override
    public GameContentDiscoveryStoppedEvent deserialize(Map<String, Object> eventData) {
        return new GameContentDiscoveryStoppedEvent(
                new GameProviderId((String) eventData.get("gameProviderId")),
                new GameContentDiscoveryResult(
                        new GameProviderId((String) eventData.get("gameProviderId")),
                        LocalDateTime.parse((String) eventData.get("startedAt")),
                        LocalDateTime.parse((String) eventData.get("stoppedAt")),
                        GameContentDiscoveryOutcome.valueOf((String) eventData.get("discoveryOutcome")),
                        LocalDateTime.parse((String) eventData.get("lastSuccessfulDiscoveryCompletedAt")),
                        (Integer) eventData.get("gamesDiscovered"),
                        (Integer) eventData.get("gameFilesDiscovered")
                )
        );
    }

    @Override
    public Class<GameContentDiscoveryStoppedEvent> getSupportedEventClass() {
        return GameContentDiscoveryStoppedEvent.class;
    }
}
