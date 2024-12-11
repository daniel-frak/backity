package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.game.domain.Game;
import lombok.NonNull;

import java.util.List;

public record GameWithFiles(
        @NonNull Game game,
        @NonNull List<GameFile> files
) {
}
