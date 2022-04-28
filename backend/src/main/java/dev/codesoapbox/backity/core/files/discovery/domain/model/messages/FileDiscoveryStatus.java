package dev.codesoapbox.backity.core.files.discovery.domain.model.messages;

import dev.codesoapbox.backity.core.shared.config.openapi.IncludeInOpenApiDocs;
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
