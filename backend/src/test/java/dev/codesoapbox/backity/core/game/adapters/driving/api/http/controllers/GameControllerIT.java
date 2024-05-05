package dev.codesoapbox.backity.core.game.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.game.application.GameFacade;
import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.fullFileDetails;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GameControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameFacade gameFacade;

    @Test
    void shouldGetGames() throws Exception {
        var gameId = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        Pagination pagination = new Pagination(0, 2);
        FileDetails fileDetails = fullFileDetails().build();
        Page<GameWithFiles> gameWithFilesPage = new Page<>(singletonList(
                new GameWithFiles(
                        new Game(gameId, "Test Game"),
                        singletonList(fileDetails)
                )
        ), 1, 2, 3, 4, 5);
        when(gameFacade.getGamesWithFiles(pagination))
                .thenReturn(gameWithFilesPage);

        mockMvc.perform(get("/api/games?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "content": [{
                                "id": "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                                "title": "Test Game",
                                "files": [{
                                  "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                  "gameId": "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                                  "sourceFileDetails": {
                                    "sourceId": "someSourceId",
                                    "originalGameTitle": "someOriginalGameTitle",
                                    "fileTitle": "someFileTitle",
                                    "version": "someVersion",
                                    "url": "someUrl",
                                    "originalFileName": "someOriginalFileName",
                                    "size": "5 KB"
                                  },
                                  "backupDetails": {
                                    "status": "DISCOVERED",
                                    "failedReason": "someFailedReason",
                                    "filePath": "someFilePath"
                                  },
                                  "dateCreated": "2022-04-29T14:15:53",
                                  "dateModified": "2023-04-29T14:15:53"
                                }]
                            }]
                        }
                        """));
    }
}