package dev.codesoapbox.backity.core.files.discovery.domain.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDiscoveryStatus {

    private String source;
    private boolean isInProgress;
}
