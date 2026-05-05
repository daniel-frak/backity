package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.core.sourcefile.domain.FileTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.FileVersion;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileUrl;
import lombok.NonNull;

public record DiscoveredFile(
        @NonNull GameProviderId gameProviderId,
        @NonNull GameTitle originalGameTitle,
        @NonNull FileTitle fileTitle,
        @NonNull FileVersion version,
        @NonNull SourceFileUrl url,
        @NonNull String originalFileName,
        @NonNull FileSize size
) {
}
