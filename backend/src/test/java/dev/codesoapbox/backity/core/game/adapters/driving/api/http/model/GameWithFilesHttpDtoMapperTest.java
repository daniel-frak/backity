package dev.codesoapbox.backity.core.game.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile.FileBackupHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile.FileBackupStatusHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile.GameProviderFileHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.fullGameFile;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameWithFilesHttpDtoMapperTest {

    private static final GameWithFilesHttpDtoMapper MAPPER = Mappers.getMapper(GameWithFilesHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        var gameId = new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281");
        var fileStringId = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var model = new GameWithFiles(
                new Game(gameId, "Test Game"),
                singletonList(fullGameFile().build())
        );

        GameWithFilesHttpDto result = MAPPER.toDto(model);

        var expectedResult = new GameWithFilesHttpDto(
                gameId.value().toString(),
                "Test Game",
                singletonList(
                        new GameFileHttpDto(
                                fileStringId,
                                gameId.value().toString(),
                                new GameProviderFileHttpDto(
                                        "someGameProviderId",
                                        "someOriginalGameTitle",
                                        "someFileTitle",
                                        "someVersion",
                                        "someUrl",
                                        "someOriginalFileName",
                                        "5 KB"
                                ),
                                new FileBackupHttpDto(
                                        FileBackupStatusHttpDto.DISCOVERED,
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