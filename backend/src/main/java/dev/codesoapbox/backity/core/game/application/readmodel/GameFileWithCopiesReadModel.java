package dev.codesoapbox.backity.core.game.application.readmodel;

import java.util.List;

public record GameFileWithCopiesReadModel(
        GameFileReadModel gameFile,
        List<FileCopyReadModel> fileCopies
) {
}
