package dev.codesoapbox.backity.core.files.discovery.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;

@IncludeInDocumentation
@AllArgsConstructor
public enum FileDiscoveryMessageTopics {

    FILE_DISCOVERY("/topic/file-discovery"),
    FILE_DISCOVERY_STATUS("/topic/file-discovery/status"),
    FILE_DISCOVERY_PROGRESS("/topic/file-discovery/progress");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
