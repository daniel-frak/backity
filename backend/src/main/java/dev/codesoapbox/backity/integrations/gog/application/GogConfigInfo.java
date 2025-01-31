package dev.codesoapbox.backity.integrations.gog.application;

import lombok.NonNull;

public record GogConfigInfo(
        @NonNull String userAuthUrl
) {
}
