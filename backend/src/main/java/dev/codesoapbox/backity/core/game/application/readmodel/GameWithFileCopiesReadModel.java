package dev.codesoapbox.backity.core.game.application.readmodel;

import java.util.List;

public record GameWithFileCopiesReadModel(
        String id,
        String title,
        List<GameFileWithCopiesReadModel> gameFilesWithCopies
) {
}
