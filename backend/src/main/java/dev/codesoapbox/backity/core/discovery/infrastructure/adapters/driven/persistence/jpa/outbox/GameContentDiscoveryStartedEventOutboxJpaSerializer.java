package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.util.Map;

// @TODO Test
public class GameContentDiscoveryStartedEventOutboxJpaSerializer
        implements DomainEventOutboxJpaSerializer<GameContentDiscoveryStartedEvent> {

    @Override
    public Map<String, Object> serialize(GameContentDiscoveryStartedEvent event) {
        return Map.of(
                "gameProviderId", event.gameProviderId().value()
        );
    }

    @Override
    public GameContentDiscoveryStartedEvent deserialize(Map<String, Object> eventData) {
        return new GameContentDiscoveryStartedEvent(
                new GameProviderId((String) eventData.get("gameProviderId"))
        );
    }

    @Override
    public Class<GameContentDiscoveryStartedEvent> getSupportedEventClass() {
        return GameContentDiscoveryStartedEvent.class;
    }
}
