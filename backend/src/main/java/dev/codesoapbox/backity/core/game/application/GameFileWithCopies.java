package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

import java.util.List;

public record GameFileWithCopies(
        GameFile gameFile,
        List<FileCopy> fileCopies
) {
}
