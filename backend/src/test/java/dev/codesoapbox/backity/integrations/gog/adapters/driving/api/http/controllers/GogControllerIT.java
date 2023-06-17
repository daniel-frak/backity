package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.config.GogControllerBeanConfig;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GogController.class)
@Import(GogControllerBeanConfig.class)
class GogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GogEmbedClient gogEmbedClient;

    @Test
    void shouldGetLibrarySize() throws Exception {
        String expectedSize = "1GB";
        when(gogEmbedClient.getLibrarySize())
                .thenReturn(expectedSize);

        mockMvc.perform(get("/api/gog/library/size"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedSize));
    }

    @Test
    void shouldGetGameDetails() throws Exception {
        var gameId = "someGameId";
        var expectedResponse = """
                {
                  "title": "someTitle",
                  "backgroundImage": "someBgImage",
                  "cdKey": "someCdKey",
                  "textInformation": "someTextInfo",
                  "files": [
                    {
                      "version": "1.0.0",
                      "manualUrl": "someUrl",
                      "name": "someFileName",
                      "size": "someFileSize"
                    }
                  ],
                  "changelog": "someChangelog"
                }
                """;

        var gameDetailsResponse = new GameDetailsResponse(
                "someTitle",
                "someBgImage",
                "someCdKey",
                "someTextInfo",
                singletonList(new GameFileDetailsResponse(
                        "1.0.0",
                        "someUrl",
                        "someFileName",
                        "someFileSize",
                        "setup.exe"
                )),
                "someChangelog"
        );

        when(gogEmbedClient.getGameDetails(gameId))
                .thenReturn(gameDetailsResponse);

        mockMvc.perform(get("/api/gog/games/" + gameId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}