package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.FileCopyStatusHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile.FileSourceHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyWithContextHttpDtoMapperTest {

    private static final FileCopyWithContextHttpDtoMapper MAPPER =
            Mappers.getMapper(FileCopyWithContextHttpDtoMapper.class);

    @Test
    void shouldDomainToDto() {
        var domain = new FileCopyWithContext(
                TestFileCopy.inProgressWithFilePath(),
                TestGameFile.gog(),
                TestGame.any()
        );

        var result = MAPPER.toDto(domain);

        FileCopyWithContextHttpDto dto = dto();

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(dto);
    }

    private FileCopyWithContextHttpDto dto() {
        return new FileCopyWithContextHttpDto(
                new FileCopyHttpDto(
                        "6df888e8-90b9-4df5-a237-0cba422c0310",
                        new FileCopyNaturalIdHttpDto(
                                "acde26d7-33c7-42ee-be16-bca91a604b48",
                                "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                        ),
                        FileCopyStatusHttpDto.IN_PROGRESS,
                        null,
                        "someFilePath",
                        LocalDateTime.parse("2022-04-29T14:15:53"),
                        LocalDateTime.parse("2023-04-29T14:15:53")
                ),
                new GameFileInFileCopyContext(
                        new FileSourceHttpDto(
                                "GOG",
                                "Game 1",
                                "Game 1 (Installer)",
                                "1.0.0",
                                "/downlink/some_game/some_file",
                                "game_1_installer.exe",
                                "5 KB"
                        )
                ),
                new GameInFileCopyContextHttpDto(
                        "Test Game"
                )
        );
    }
}