package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.gamefile.domain.FileSource;

public class TestFileSourceReadModel {

    public static FileSourceReadModel from(FileSource fileSource) {
        return new FileSourceReadModel(
                fileSource.gameProviderId().value(),
                fileSource.originalGameTitle(),
                fileSource.fileTitle(),
                fileSource.version(),
                fileSource.url(),
                fileSource.originalFileName(),
                fileSource.size().toString()
        );
    }
}