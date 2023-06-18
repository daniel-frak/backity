package dev.codesoapbox.backity.core.discovery.domain.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDiscoveryProgress {

    private String source;
    private int percentage;
    private long timeLeftSeconds;
}
