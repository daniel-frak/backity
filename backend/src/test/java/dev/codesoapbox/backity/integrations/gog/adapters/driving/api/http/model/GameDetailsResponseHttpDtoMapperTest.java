package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameDetailsResponseHttpDtoMapperTest {

    private static final GameDetailsResponseHttpDtoMapper MAPPER =
            Mappers.getMapper(GameDetailsResponseHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GameDetailsResponse domain = domainObject();

        GameDetailsResponseHttpDto result = MAPPER.toDto(domain);

        GameDetailsResponseHttpDto expectedResult = dto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GameDetailsResponse domainObject() {
        return new GameDetailsResponse(
                "someTitle",
                "someBackgroundImage",
                "someCdKey",
                "someTextInformation",
                singletonList(new GameFileResponse(
                        "1.0.0",
                        "someManualUrl",
                        "someName",
                        "100 KB",
                        "Game 1 (Installer)"
                )),
                "someChangelog"
        );
    }

    private GameDetailsResponseHttpDto dto() {
        return new GameDetailsResponseHttpDto(
                "someTitle",
                "someBackgroundImage",
                "someCdKey",
                "someTextInformation",
                singletonList(new GameFileResponseHttpDto(
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