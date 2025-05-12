package dev.codesoapbox.backity.gameproviders.gog.application;

import lombok.NonNull;

public record GogConfigInfo(
        @NonNull String userAuthUrl
) {
}
