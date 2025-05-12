package dev.codesoapbox.backity.gameproviders.gog.domain;

import java.util.List;

public record GogGameWithFiles(
        String title,
        String backgroundImage,
        String cdKey,
        String textInformation,
        List<GogGameFile> files,
        String changelog
) {
}
