package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import lombok.NonNull;

import java.util.List;

public record GameFileWithCopies(
        @NonNull GameFile gameFile,
        @NonNull List<FileCopyWithProgress> fileCopiesWithProgress
) {
}
