package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import lombok.AllArgsConstructor;

@IncludeInDocumentation
@AllArgsConstructor
public enum FileDiscoveryWebSocketTopics {

    FILE_DISCOVERED("/topic/file-discovery/file-discovered"),
    FILE_DISCOVERY_STATUS_CHANGED("/topic/file-discovery/file-status-changed"),
    FILE_DISCOVERY_PROGRESS_UPDATE("/topic/file-discovery/progress-update");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
