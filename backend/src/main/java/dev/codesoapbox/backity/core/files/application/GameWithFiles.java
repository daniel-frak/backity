package dev.codesoapbox.backity.core.files.application;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.game.Game;

import java.util.List;

public record GameWithFiles(
        Game game,
        List<GameFileDetails> gameFiles
) {
}
