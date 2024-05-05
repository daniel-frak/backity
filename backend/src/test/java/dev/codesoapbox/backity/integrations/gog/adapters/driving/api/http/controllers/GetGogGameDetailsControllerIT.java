package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import dev.codesoapbox.backity.integrations.gog.application.GetGogGameDetailsUseCase;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.FileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GetGogGameDetailsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGogGameDetailsUseCase useCase;

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
                singletonList(new FileDetailsResponse(
                        "1.0.0",
                        "someUrl",
                        "someFileName",
                        "someFileSize",
                        "setup.exe"
                )),
                "someChangelog"
        );

        when(useCase.getGameDetails(gameId))
                .thenReturn(gameDetailsResponse);

        mockMvc.perform(get("/api/gog/games/" + gameId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}