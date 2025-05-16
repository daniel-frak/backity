package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import dev.codesoapbox.backity.gameproviders.gog.application.usecases.GetGogGameDetailsUseCase;
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
class GetGogGamesWithFilesControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGogGameDetailsUseCase useCase;

    @Test
    void shouldGetGameDetails() throws Exception {
        var gameId = "someGameId";
        var expectedResponse = """
                {
                  "title": "someGameTitle",
                  "backgroundImage": "someBgImage",
                  "cdKey": "some-cd-key",
                  "textInformation": "someTextInfo",
                  "files": [
                    {
                      "version": "1.0.0",
                      "manualUrl": "/downlink/some_game/some_file",
                      "fileTitle": "someFileTitle",
                      "size": "someFileSize"
                    }
                  ],
                  "changelog": "someChangelog"
                }
                """;

        var gogGameDetails = new GogGameWithFiles(
                "someGameTitle",
                "someBgImage",
                "some-cd-key",
                "someTextInfo",
                singletonList(new GogGameFile(
                        "1.0.0",
                        "/downlink/some_game/some_file",
                        "someFileTitle",
                        "someFileSize",
                        "setup.exe"
                )),
                "someChangelog"
        );

        when(useCase.getGameDetails(gameId))
                .thenReturn(gogGameDetails);

        mockMvc.perform(get("/api/gog/games/" + gameId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}