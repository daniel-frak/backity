package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.sourcefile.domain.*;
import lombok.NonNull;

public record DiscoveredFile(
        @NonNull GameProviderId gameProviderId,
        @NonNull GameTitle originalGameTitle,
        @NonNull FileTitle fileTitle,
        @NonNull FileVersion version,
        @NonNull SourceFileUrl url,
        @NonNull FileName originalFileName,
        @NonNull FileSize size
) {
}
