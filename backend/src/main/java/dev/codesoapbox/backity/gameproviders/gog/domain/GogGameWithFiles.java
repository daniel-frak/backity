package dev.codesoapbox.backity.gameproviders.gog.domain;

import lombok.NonNull;

import java.util.List;

public record GogGameWithFiles(
        @NonNull String title,
        String backgroundImage,
        String cdKey,
        String textInformation,
        @NonNull List<GogGameFile> files,
        String changelog
) {
}
