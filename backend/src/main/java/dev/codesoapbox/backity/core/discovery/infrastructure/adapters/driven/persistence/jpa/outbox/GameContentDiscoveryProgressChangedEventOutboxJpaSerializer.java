package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.time.Duration;
import java.util.Map;

// @TODO Test
public class GameContentDiscoveryProgressChangedEventOutboxJpaSerializer
        implements DomainEventOutboxJpaSerializer<GameContentDiscoveryProgressChangedEvent> {

    @Override
    public Map<String, Object> serialize(GameContentDiscoveryProgressChangedEvent event) {
        return Map.of(
                "gameProviderId", event.gameProviderId().value(),
                "percentage", event.percentage(),
                "timeLeftMillis", event.timeLeft().toMillis(),
                "gamesDiscovered", event.gamesDiscovered(),
                "gameFilesDiscovered", event.gameFilesDiscovered()
        );
    }

    @Override
    public GameContentDiscoveryProgressChangedEvent deserialize(Map<String, Object> eventData) {
        return new GameContentDiscoveryProgressChangedEvent(
                new GameProviderId((String) eventData.get("gameProviderId")),
                (Integer) eventData.get("percentage"),
                Duration.ofMillis((Integer) eventData.get("timeLeftMillis")), // @TODO Should be long but fails
                (Integer) eventData.get("gamesDiscovered"), // @TODO Should be long but fails
                (Integer) eventData.get("gameFilesDiscovered")
        );
    }

    @Override
    public Class<GameContentDiscoveryProgressChangedEvent> getSupportedEventClass() {
        return GameContentDiscoveryProgressChangedEvent.class;
    }
}
