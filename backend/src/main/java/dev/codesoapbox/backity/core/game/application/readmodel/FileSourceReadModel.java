package dev.codesoapbox.backity.core.game.application.readmodel;

public record FileSourceReadModel(
        String gameProviderId,
        String originalGameTitle,
        String fileTitle,
        String version,
        String url,
        String originalFileName,
        String size
) {
}
