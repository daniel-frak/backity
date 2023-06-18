package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IncludeInDocumentation
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDiscoveryProgressUpdateMessage {

    private String source;
    private int percentage;
    private long timeLeftSeconds;
}
