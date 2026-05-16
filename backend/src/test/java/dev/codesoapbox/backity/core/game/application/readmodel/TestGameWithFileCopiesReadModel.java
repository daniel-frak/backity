package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.game.domain.Game;
import lombok.Builder;

import java.util.List;

import static java.util.Collections.emptyList;

@Builder(builderClassName = "Builder", builderMethodName = "", buildMethodName = "internalBuild", setterPrefix = "with")
public class TestGameWithFileCopiesReadModel {

    @lombok.Builder.Default
    private String id = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";

    @lombok.Builder.Default
    private String title = "Test Game";

    private List<SourceFileWithCopiesReadModel> sourceFilesWithCopies;

    public static GameWithFileCopiesReadModel withNoSourceFiles() {
        return withNoSourceFilesBuilder().build();
    }

    public static TestGameWithFileCopiesReadModel.Builder withNoSourceFilesBuilder() {
        return new TestGameWithFileCopiesReadModel.Builder()
                .withSourceFilesWithCopies(emptyList());
    }

    public static class Builder {

        public Builder withValuesFrom(Game game) {
            return withId(game.getId().value().toString())
                    .withTitle(game.getTitle().value());
        }

        public GameWithFileCopiesReadModel build() {
            TestGameWithFileCopiesReadModel temp = internalBuild();
            return new GameWithFileCopiesReadModel(temp.id, temp.title, temp.sourceFilesWithCopies);
        }
    }
}