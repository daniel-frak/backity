package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.application.GameFileWithCopies;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDto;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyNaturalIdHttpDto;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileCopyStatusHttpDto;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileSourceHttpDto;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameWithFileCopiesHttpDtoMapperTest {

    private static final GameWithFileCopiesHttpDtoMapper MAPPER =
            Mappers.getMapper(GameWithFileCopiesHttpDtoMapper.class);

    @Test
    void shouldMapDomainToDto() {
        GameWithFileCopies domain = domain();

        GameWithFileCopiesHttpDto result = MAPPER.toDto(domain);

        GameWithFileCopiesHttpDto expectedResult = dto();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameWithFileCopies domain() {
        Game game = TestGame.any();
        return new GameWithFileCopies(
                game,
                singletonList(
                        new GameFileWithCopies(
                                TestGameFile.gogBuilder()
                                        .gameId(game.getId())
                                        .build(),
                                List.of(
                                        TestFileCopy.discovered(),
                                        TestFileCopy.successful(),
                                        TestFileCopy.failed()
                                )
                        )
                )
        );
    }

    private GameWithFileCopiesHttpDto dto() {
        return new GameWithFileCopiesHttpDto(
                "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                "Test Game",
                singletonList(
                        new GameFileWithCopiesHttpDto(
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
                                        LocalDateTime.parse("2022-04-29T14:15:53"),
                                        LocalDateTime.parse("2023-04-29T14:15:53")
                                ),
                                List.of(
                                        fileCopyDto(FileCopyStatusHttpDto.DISCOVERED,
                                                null, null),
                                        fileCopyDto(FileCopyStatusHttpDto.SUCCESS,
                                                "someFilePath", null),
                                        fileCopyDto(FileCopyStatusHttpDto.FAILED,
                                                null, "someFailedReason")
                                )
                        )
                )
        );
    }

    private FileCopyHttpDto fileCopyDto(FileCopyStatusHttpDto status, String filePath, String failedReason) {
        return new FileCopyHttpDto(
                "6df888e8-90b9-4df5-a237-0cba422c0310",
                new FileCopyNaturalIdHttpDto(
                        "acde26d7-33c7-42ee-be16-bca91a604b48",
                        "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                ),
                status,
                failedReason,
                filePath,
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    @Test
    void toDtoShouldReturnNullGivenNull() {
        assertThat(MAPPER.toDto(null))
                .isNull();
    }
}