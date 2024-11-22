package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.fullGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class GameFileHttpDtoMapperTest {

    private static final GameFileHttpDtoMapper MAPPER = Mappers.getMapper(GameFileHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GameFile domain = fullGameFile().build();

        GameFileHttpDto result = MAPPER.toDto(domain);

        var expectedResult = new GameFileHttpDto(
                "acde26d7-33c7-42ee-be16-bca91a604b48",
                "1eec1c19-25bf-4094-b926-84b5bb8fa281",
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
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}