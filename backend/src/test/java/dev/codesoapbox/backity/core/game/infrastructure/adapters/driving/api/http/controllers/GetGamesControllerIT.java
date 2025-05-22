package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.application.GameFileWithCopies;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopies;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.domain.TestPage;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
        Page<GameWithFileCopies> gameWithFileCopiesPage = TestPage.of(List.of(
                new GameWithFileCopies(
                        new Game(gameId, null, null, "Test Game"),
                        List.of(new GameFileWithCopies(TestGameFile.gog(),
                                List.of(TestFileCopy.discovered(), TestFileCopy.successful(), TestFileCopy.failed()))
                        ))), pagination);
        when(getGamesWithFilesUseCase.getGamesWithFiles(pagination))
                .thenReturn(gameWithFileCopiesPage);

        mockMvc.perform(get("/api/games?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "content": [
                            {
                                "id": "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                                "title": "Test Game",
                                "gameFilesWithCopies": [
                                        {
                                            "gameFile": {
                                              "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                              "gameId": "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                                              "fileSource": {
                                                "gameProviderId": "GOG",
                                                "originalGameTitle": "Game 1",
                                                "fileTitle": "Game 1 (Installer)",
                                                "version": "1.0.0",
                                                "url": "/downlink/some_game/some_file",
                                                "originalFileName": "game_1_installer.exe",
                                                "size": "5 KB"
                                              },
                                              "dateCreated": "2022-04-29T14:15:53",
                                              "dateModified": "2023-04-29T14:15:53"
                                            },
                                            "fileCopies": [
                                              {
                                                "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                                                "naturalId": {
                                                    "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                                    "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                                                },
                                                "status": "DISCOVERED",
                                                "failedReason": null,
                                                "filePath": null,
                                                "dateCreated": "2022-04-29T14:15:53",
                                                "dateModified": "2023-04-29T14:15:53"
                                              },
                                              {
                                                "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                                                "naturalId": {
                                                    "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                                    "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                                                },
                                                "status": "SUCCESS",
                                                "failedReason": null,
                                                "filePath": "someFilePath",
                                                "dateCreated": "2022-04-29T14:15:53",
                                                "dateModified": "2023-04-29T14:15:53"
                                              },
                                              {
                                                "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                                                "naturalId": {
                                                    "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                                    "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                                                },
                                                "status": "FAILED",
                                                "failedReason": "someFailedReason",
                                                "filePath": null,
                                                "dateCreated": "2022-04-29T14:15:53",
                                                "dateModified": "2023-04-29T14:15:53"
                                              }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                        """));
    }
}