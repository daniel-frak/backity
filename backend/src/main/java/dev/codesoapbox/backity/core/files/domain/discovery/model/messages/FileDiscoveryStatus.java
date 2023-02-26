package dev.codesoapbox.backity.core.files.domain.discovery.model.messages;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IncludeInDocumentation
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDiscoveryStatus {

    private String source;
    private boolean isInProgress;
}
