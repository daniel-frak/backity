package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GogGameWithFilesHttpDtoMapperTest {

    private static final GogGameWithFilesHttpDtoMapper MAPPER =
            Mappers.getMapper(GogGameWithFilesHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GogGameWithFiles domain = domainObject();

        GogGameWithFilesHttpDto result = MAPPER.toDto(domain);

        GogGameWithFilesHttpDto expectedResult = dto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GogGameWithFiles domainObject() {
        return new GogGameWithFiles(
                "someTitle",
                "someBackgroundImage",
                "someCdKey",
                "someTextInformation",
                singletonList(new GogGameFile(
                        "1.0.0",
                        "someManualUrl",
                        "someName",
                        "100 KB",
                        "Game 1 (Installer)"
                )),
                "someChangelog"
        );
    }

    private GogGameWithFilesHttpDto dto() {
        return new GogGameWithFilesHttpDto(
                "someTitle",
                "someBackgroundImage",
                "someCdKey",
                "someTextInformation",
                singletonList(new GogGameFileHttpDto(
                        "1.0.0",
                        "someManualUrl",
                        "someName",
                        "100 KB",
                        "Game 1 (Installer)"
                )),
                "someChangelog"
        );
    }
}