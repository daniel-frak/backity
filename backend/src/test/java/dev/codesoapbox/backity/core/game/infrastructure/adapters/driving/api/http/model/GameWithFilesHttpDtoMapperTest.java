package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.FileBackupHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.FileBackupStatusHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.FileSourceHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameWithFilesHttpDtoMapperTest {

    private static final GameWithFilesHttpDtoMapper MAPPER = Mappers.getMapper(GameWithFilesHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GameWithFiles model = domainObject();

        GameWithFilesHttpDto result = MAPPER.toDto(model);

        var expectedResult = dto();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameWithFiles domainObject() {
        Game game = TestGame.any();
        return new GameWithFiles(
                game,
                singletonList(TestGameFile.fullBuilder()
                        .gameId(game.getId())
                        .build())
        );
    }

    private GameWithFilesHttpDto dto() {
        return new GameWithFilesHttpDto(
                "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                "Test Game",
                singletonList(
                        new GameFileHttpDto(
                                "acde26d7-33c7-42ee-be16-bca91a604b48",
                                "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                                new FileSourceHttpDto(
                                        "GOG",
                                        "Game 1",
                                        "Game 1 (Installer)",
                                        "1.0.0",
                                        "/downlink/some_game/some_file",
                                        "game_1_installer.exe",
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
    }

    @Test
    void toDtoShouldReturnNullGivenNull() {
        assertThat(MAPPER.toDto(null))
                .isNull();
    }
}