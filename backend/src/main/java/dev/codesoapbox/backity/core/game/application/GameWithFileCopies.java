package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.game.domain.Game;
import lombok.NonNull;

import java.util.List;

public record GameWithFileCopies(
        @NonNull Game game,
        @NonNull List<GameFileWithCopies> gameFilesWithCopies
) {
}
