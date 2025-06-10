package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@IncludeInDocumentation
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public enum GameContentDiscoveryWebSocketTopics {

    GAME_CONTENT_DISCOVERY_STARTED("/topic/game-content-discovery/discovery-started"),
    GAME_CONTENT_DISCOVERY_STOPPED("/topic/game-content-discovery/discovery-stopped"),
    GAME_CONTENT_DISCOVERY_PROGRESS_CHANGED("/topic/game-content-discovery/progress-update");

    private final String wsDestination;

    @Override
    public String toString() {
        return wsDestination;
    }
}
