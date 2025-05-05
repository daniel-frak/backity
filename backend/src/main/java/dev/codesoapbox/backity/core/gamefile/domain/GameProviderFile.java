package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.NonNull;

public record GameProviderFile(
        @NonNull GameProviderId gameProviderId,
        @NonNull String originalGameTitle,
        @NonNull String fileTitle,
        @NonNull String version,
        @NonNull String url,
        @NonNull String originalFileName,
        @NonNull FileSize size
) {
}
