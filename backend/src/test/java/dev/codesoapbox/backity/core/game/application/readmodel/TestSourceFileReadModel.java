package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;

public class TestSourceFileReadModel {

    public static SourceFileReadModel from(SourceFile sourceFile) {
        return new SourceFileReadModel(
                sourceFile.getId().value().toString(),
                sourceFile.getGameId().value().toString(),
                sourceFile.getGameProviderId().value(),
                sourceFile.getOriginalGameTitle().value(),
                sourceFile.getFileTitle().value(),
                sourceFile.getVersion().value(),
                sourceFile.getUrl().value(),
                sourceFile.getOriginalFileName().value(),
                sourceFile.getSize().toString(),
                sourceFile.getDateCreated(),
                sourceFile.getDateModified()
        );
    }
}