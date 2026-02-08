package dev.codesoapbox.backity.core.game.application.readmodel;

import java.util.List;

public record SourceFileWithCopiesReadModel(
        SourceFileReadModel sourceFile,
        List<FileCopyReadModel> fileCopies
) {
}
