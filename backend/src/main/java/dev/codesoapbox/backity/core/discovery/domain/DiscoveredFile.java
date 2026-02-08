package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.exceptions.DiscoveredFileUrlEmptyException;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;

public record DiscoveredFile(
        @NonNull GameProviderId gameProviderId,
        @NonNull String originalGameTitle,
        @NonNull String fileTitle,
        @NonNull String version,
        @NonNull String url,
        @NonNull String originalFileName,
        @NonNull FileSize size
) {

    public DiscoveredFile {
        if (Strings.isBlank(url)) {
            throw new DiscoveredFileUrlEmptyException(gameProviderId);
        }
    }
}
