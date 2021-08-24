package dev.codesoapbox.backity.core.files.discovery.application.messages;

import dev.codesoapbox.backity.core.shared.infrastructure.config.openapi.IncludeInOpenApiDocs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IncludeInOpenApiDocs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDiscoveryStatus {

    private String source;
    private boolean isInProgress;
}
