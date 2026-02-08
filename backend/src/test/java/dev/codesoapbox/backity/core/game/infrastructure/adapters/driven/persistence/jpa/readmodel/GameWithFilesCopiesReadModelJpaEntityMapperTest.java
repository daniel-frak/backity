package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.application.readmodel.*;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameWithFilesCopiesReadModelJpaEntityMapperTest {

    private static final GameWithFilesCopiesReadModelJpaEntityMapper MAPPER =
            Mappers.getMapper(GameWithFilesCopiesReadModelJpaEntityMapper.class);

    @Test
    void shouldMapToReadModel() {
        GameWithFileCopiesReadModelJpaEntity entity = entity();

        GameWithFileCopiesReadModel result = MAPPER.toReadModel(entity);

        GameWithFileCopiesReadModel expectedResult = readModel();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GameWithFileCopiesReadModelJpaEntity entity() {
        UUID sourceFileId = UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48");
        UUID fileCopyId = UUID.fromString("6df888e8-90b9-4df5-a237-0cba422c0310");
        UUID backupTargetId = UUID.fromString("eda52c13-ddf7-406f-97d9-d3ce2cab5a76");
        UUID gameId = UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        return new GameWithFileCopiesReadModelJpaEntity(
                gameId,
                "Test Game",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53"),
                List.of(
                        new SourceFileWithCopiesReadModelJpaEntity(
                                sourceFileId,
                                gameId,
                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                LocalDateTime.parse("2023-04-29T14:15:53"),
                                "GOG",
                                "Game 1",
                                "Game 1 (Installer)",
                                "1.0.0",
                                "/downlink/some_game/some_file",
                                "game_1_installer.exe",
                                5120L,
                                List.of(
                                        new FileCopyReadModelJpaEntity(
                                                fileCopyId,
                                                new FileCopyNaturalIdReadModelJpaEmbeddable(
                                                        sourceFileId,
                                                        backupTargetId
                                                ),
                                                FileCopyStatus.STORED_INTEGRITY_UNKNOWN,
                                                null,
                                                "someFilePath",
                                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                                LocalDateTime.parse("2023-04-29T14:15:53")
                                        ),
                                        new FileCopyReadModelJpaEntity(
                                                fileCopyId,
                                                new FileCopyNaturalIdReadModelJpaEmbeddable(
                                                        sourceFileId,
                                                        backupTargetId
                                                ),
                                                FileCopyStatus.FAILED,
                                                "someFailedReason",
                                                null,
                                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                                LocalDateTime.parse("2023-04-29T14:15:53")
                                        )
                                )
                        )
                )

        );
    }

    private GameWithFileCopiesReadModel readModel() {
        return TestGameWithFileCopiesReadModel.withNoSourceFilesBuilder()
                .withId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")
                .withSourceFilesWithCopies(List.of(
                        new SourceFileWithCopiesReadModel(
                                TestSourceFileReadModel.from(TestSourceFile.gogBuilder()
                                        .gameId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                                        .build()),
                                List.of(
                                        TestFileCopyReadModel.from(TestFileCopy.storedIntegrityUnknown()),
                                        TestFileCopyReadModel.from(TestFileCopy.failedWithoutFilePath())
                                )
                        )
                ))
                .build();
    }
}