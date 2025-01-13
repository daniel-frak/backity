package dev.codesoapbox.backity.core.game.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.game.application.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.application.GameWithFiles;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.domain.TestPage;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GetGamesControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGamesWithFilesUseCase getGamesWithFilesUseCase;

    @Test
    void shouldGetGames() throws Exception {
        var gameId = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        var pagination = new Pagination(0, 2);
        GameFile gameFile = TestGameFile.full();
        Page<GameWithFiles> gameWithFilesPage = TestPage.of(List.of(
                new GameWithFiles(
                        new Game(gameId, "Test Game"),
                        singletonList(gameFile)
                )), pagination);
        when(getGamesWithFilesUseCase.getGamesWithFiles(pagination))
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
                                  "gameProviderFile": {
                                    "gameProviderId": "GOG",
                                    "originalGameTitle": "Game 1",
                                    "fileTitle": "Game 1 (Installer)",
                                    "version": "1.0.0",
                                    "url": "http://some.url",
                                    "originalFileName": "game_1_installer.exe",
                                    "size": "5 KB"
                                  },
                                  "fileBackup": {
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