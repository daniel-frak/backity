package dev.codesoapbox.backity.gameproviders.gog.domain;

import lombok.NonNull;

public record GogGameFile(
        @NonNull String version,
        @NonNull String manualUrl,
        @NonNull String fileTitle,
        @NonNull String size,
        @NonNull String fileName
) {
}
