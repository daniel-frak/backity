package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.TestGogGameWithFiles;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GogGameWithFilesHttpDtoMapperTest {

    private static final GogGameWithFilesHttpDtoMapper MAPPER =
            Mappers.getMapper(GogGameWithFilesHttpDtoMapper.class);

    @Test
    void shouldMapMinimalToDto() {
        GogGameWithFiles domain = TestGogGameWithFiles.minimal();

        GogGameWithFilesHttpDto result = MAPPER.toDto(domain);

        GogGameWithFilesHttpDto expectedResult = minimalDto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GogGameWithFilesHttpDto minimalDto() {
        return new GogGameWithFilesHttpDto(
                "Test Game",
                null,
                null,
                null,
                emptyList(),
                null
        );
    }

    @Test
    void shouldMapFullToDto() {
        GogGameWithFiles domain = TestGogGameWithFiles.fullWithMinimalFile();

        GogGameWithFilesHttpDto result = MAPPER.toDto(domain);

        GogGameWithFilesHttpDto expectedResult = fullDto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GogGameWithFilesHttpDto fullDto() {
        return new GogGameWithFilesHttpDto(
                "Test Game",
                "//images-4.gog.com/somePath",
                "some-cd-key",
                "Some text information",
                singletonList(new GogGameFileHttpDto(
                        "unknown",
                        "/downlink/some_game/some_file",
                        "Game 1 (Installer)",
                        "1 MB",
                        "game_1_installer.exe"
                )),
                "Some changelog"
        );
    }
}