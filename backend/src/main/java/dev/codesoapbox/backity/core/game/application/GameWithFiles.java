package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;

import java.util.List;

public record GameWithFiles(
        Game game,
        List<GameFileDetails> gameFiles
) {
}
