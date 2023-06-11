package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.application.GameWithFiles;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameWithFilesJsonMapperTest {

    private static final GameWithFilesJsonMapper MAPPER = Mappers.getMapper(GameWithFilesJsonMapper.class);

    @Test
    void shouldMapToDto() {
        var gameId = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        var gameFileStringId = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var model = new GameWithFiles(
                new Game(gameId, "Test Game"),
                singletonList(TestGameFileDetails.FULL_GAME_FILE_DETAILS.get())
        );

        GameWithFilesJson result = MAPPER.toDto(model);

        var expectedResult = new GameWithFilesJson(
                gameId.value().toString(),
                "Test Game",
                singletonList(
                        new GameFileDetailsJson(
                                gameFileStringId,
                                gameId.value().toString(),
                                new SourceFileDetailsJson(
                                        "someSourceId",
                                        "someOriginalGameTitle",
                                        "someFileTitle",
                                        "someVersion",
                                        "someUrl",
                                        "someOriginalFileName",
                                        "5 KB"
                                ),
                                new BackupDetailsJson(
                                        FileBackupStatus.DISCOVERED,
                                        "someFailedReason",
                                        "someFilePath"
                                ),
                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                LocalDateTime.parse("2023-04-29T14:15:53")
                        )
                )
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void toDtoShouldReturnNullGivenNull() {
        assertThat(MAPPER.toDto(null))
                .isNull();
    }
}