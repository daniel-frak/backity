package dev.codesoapbox.backity.core.game.application.readmodel;

import java.time.LocalDateTime;

public record GameFileReadModel(
        String id,
        String gameId,
        FileSourceReadModel fileSource,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
