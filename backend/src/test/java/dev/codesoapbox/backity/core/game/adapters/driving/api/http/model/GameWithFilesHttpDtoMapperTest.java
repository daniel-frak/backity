package dev.codesoapbox.backity.core.game.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefiledetails.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.BackupDetailsHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.GameFileDetailsHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails.SourceFileDetailsHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.fullFileDetails;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameWithFilesHttpDtoMapperTest {

    private static final GameWithFilesHttpDtoMapper MAPPER = Mappers.getMapper(GameWithFilesHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        var gameId = new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"));
        var gameFileStringId = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var model = new GameWithFiles(
                new Game(gameId, "Test Game"),
                singletonList(fullFileDetails().build())
        );

        GameWithFilesHttpDto result = MAPPER.toDto(model);

        var expectedResult = new GameWithFilesHttpDto(
                gameId.value().toString(),
                "Test Game",
                singletonList(
                        new GameFileDetailsHttpDto(
                                gameFileStringId,
                                gameId.value().toString(),
                                new SourceFileDetailsHttpDto(
                                        "someSourceId",
                                        "someOriginalGameTitle",
                                        "someFileTitle",
                                        "someVersion",
                                        "someUrl",
                                        "someOriginalFileName",
                                        "5 KB"
                                ),
                                new BackupDetailsHttpDto(
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