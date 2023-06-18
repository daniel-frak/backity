package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameDetailsResponseHttpDtoMapperTest {

    private static final GameDetailsResponseHttpDtoMapper MAPPER =
            Mappers.getMapper(GameDetailsResponseHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        var domain = new GameDetailsResponse();
        domain.setTitle("someTitle");
        domain.setBackgroundImage("someBackgroundImage");
        domain.setCdKey("someCdKey");
        domain.setTextInformation("someTextInformation");
        GameFileDetailsResponse gameFileDetailsResponse = new GameFileDetailsResponse();
        gameFileDetailsResponse.setVersion("someVersion");
        gameFileDetailsResponse.setManualUrl("someManualUrl");
        gameFileDetailsResponse.setName("someName");
        gameFileDetailsResponse.setFileTitle("someFileTitle");
        gameFileDetailsResponse.setSize("100 KB");
        domain.setFiles(singletonList(gameFileDetailsResponse));
        domain.setChangelog("someChangelog");

        var result = MAPPER.toDto(domain);

        assertEquals("someTitle", result.title());
        assertEquals("someBackgroundImage", result.backgroundImage());
        assertEquals("someCdKey", result.cdKey());
        assertEquals("someTextInformation", result.textInformation());
        assertEquals("someVersion", result.files().get(0).version());
        assertEquals("someManualUrl", result.files().get(0).manualUrl());
        assertEquals("someName", result.files().get(0).name());
        assertEquals("someFileTitle", result.files().get(0).fileTitle());
        assertEquals("100 KB", result.files().get(0).size());
        assertEquals("someChangelog", result.changelog());
    }
}