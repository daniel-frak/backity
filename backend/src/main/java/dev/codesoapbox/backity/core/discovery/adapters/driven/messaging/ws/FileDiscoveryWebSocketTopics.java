package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws;

import dev.codesoapbox.backity.shared.adapters.driven.messaging.IncludeInDocumentation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@IncludeInDocumentation
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public enum FileDiscoveryWebSocketTopics {

    FILE_DISCOVERED("/topic/file-discovery/file-discovered"),
    FILE_DISCOVERY_STATUS_CHANGED("/topic/file-discovery/file-status-changed"),
    FILE_DISCOVERY_PROGRESS_CHANGED("/topic/file-discovery/progress-update");

    private final String wsDestination;

    @Override
    public String toString() {
        return wsDestination;
    }
}
