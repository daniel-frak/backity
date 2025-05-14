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

    FILE_DISCOVERED("/topic/game-content-discovery/file-discovered"),
    GAME_CONTENT_DISCOVERY_STATUS_CHANGED("/topic/game-content-discovery/status-changed"),
    GAME_CONTENT_DISCOVERY_PROGRESS_CHANGED("/topic/game-content-discovery/progress-update");

    private final String wsDestination;

    @Override
    public String toString() {
        return wsDestination;
    }
}
