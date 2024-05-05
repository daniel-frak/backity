package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.game.domain.Game;

import java.util.List;

public record GameWithFiles(
        Game game,
        List<FileDetails> files
) {
}
