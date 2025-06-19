package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

public class TestGameFileReadModel {

    public static GameFileReadModel from(GameFile gameFile) {
        return new GameFileReadModel(
                gameFile.getId().value().toString(),
                gameFile.getGameId().value().toString(),
                TestFileSourceReadModel.from(gameFile.getFileSource()),
                gameFile.getDateCreated(),
                gameFile.getDateModified()
        );
    }
}