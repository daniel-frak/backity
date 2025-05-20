package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileHttpDtoMapperTest {

    private static final GameFileHttpDtoMapper MAPPER = Mappers.getMapper(GameFileHttpDtoMapper.class);

    @Test
    void shouldMapSuccessfulToDto() {
        GameFile domain = TestGameFile.successful();

        GameFileHttpDto result = MAPPER.toDto(domain);

        var expectedResult = new GameFileHttpDto(
                "acde26d7-33c7-42ee-be16-bca91a604b48",
                "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                new FileSourceHttpDto(
                        "GOG",
                        "Game 1",
                        "Game 1 (Installer)",
                        "1.0.0",
                        "/downlink/some_game/some_file",
                        "game_1_installer.exe",
                        "5 KB"
                ),
                new FileCopyHttpDto(
                        FileBackupStatusHttpDto.SUCCESS,
                        null,
                        "someFilePath"
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldMapFailedToDto() {
        GameFile domain = TestGameFile.failed();

        GameFileHttpDto result = MAPPER.toDto(domain);

        var expectedResult = new GameFileHttpDto(
                "acde26d7-33c7-42ee-be16-bca91a604b48",
                "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                new FileSourceHttpDto(
                        "GOG",
                        "Game 1",
                        "Game 1 (Installer)",
                        "1.0.0",
                        "/downlink/some_game/some_file",
                        "game_1_installer.exe",
                        "5 KB"
                ),
                new FileCopyHttpDto(
                        FileBackupStatusHttpDto.FAILED,
                        "someFailedReason",
                        null
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}