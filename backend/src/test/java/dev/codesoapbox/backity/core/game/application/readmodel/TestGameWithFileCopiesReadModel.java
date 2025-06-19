package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.game.domain.Game;
import lombok.Builder;

import java.util.List;

import static java.util.Collections.emptyList;

@Builder(builderClassName = "Builder", builderMethodName = "withNoGameFilesBuilder",
        buildMethodName = "internalBuilder", setterPrefix = "with")
public class TestGameWithFileCopiesReadModel {

    @lombok.Builder.Default
    private String id = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";

    @lombok.Builder.Default
    private String title = "Test Game";

    @lombok.Builder.Default
    private List<GameFileWithCopiesReadModel> gameFilesWithCopies = emptyList();

    public static GameWithFileCopiesReadModel withNoGameFiles() {
        return withNoGameFilesBuilder().build();
    }

    public static TestGameWithFileCopiesReadModel.Builder withNoGameFilesBuilder() {
        return new TestGameWithFileCopiesReadModel.Builder();
    }

    public static class Builder {

        public Builder withValuesFrom(Game game) {
            return withId(game.getId().value().toString())
                    .withTitle(game.getTitle());
        }

        public GameWithFileCopiesReadModel build() {
            TestGameWithFileCopiesReadModel temp = internalBuilder();
            return new GameWithFileCopiesReadModel(temp.id, temp.title, temp.gameFilesWithCopies);
        }
    }
}