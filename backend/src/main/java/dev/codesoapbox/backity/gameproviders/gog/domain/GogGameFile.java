package dev.codesoapbox.backity.gameproviders.gog.domain;

public record GogGameFile(
        String version,
        String manualUrl,
        String name,
        String size,
        String fileTitle
) {
}
