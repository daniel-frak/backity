package dev.codesoapbox.backity.core.game.application.readmodel;

import java.time.LocalDateTime;

public record SourceFileReadModel(
        String id,
        String gameId,
        String gameProviderId,
        String originalGameTitle,
        String fileTitle,
        String version,
        String url,
        String originalFileName,
        String size,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
