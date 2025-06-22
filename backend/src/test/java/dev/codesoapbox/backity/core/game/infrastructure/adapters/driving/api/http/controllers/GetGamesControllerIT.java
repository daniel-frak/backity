package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backup.domain.TestFileCopyReplicationProgress;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesAndReplicationProgresses;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.TestGameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.readmodel.GameFileWithCopiesReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.TestFileCopyReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.TestGameFileReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.TestGameWithFileCopiesReadModel;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
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

    private static final FileCopyId IN_PROGRESS_FILE_COPY_ID =
            new FileCopyId("d6c81f47-e2b6-424b-b997-0dad6aa372c7");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGamesWithFilesUseCase getGamesWithFilesUseCase;

    @Test
    void shouldGetGamesWithNoFilters() throws Exception {
        var gameId = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        var pagination = new Pagination(0, 2);
        GameWithFileCopiesSearchFilter expectedFilter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(null);
        Page<GameWithFileCopiesAndReplicationProgresses> gameWithFileCopiesPage = TestPage.of(List.of(
                new GameWithFileCopiesAndReplicationProgresses(
                        TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                                .withId(gameId.toString())
                                .withTitle("Test Game")
                                .withGameFilesWithCopies(List.of(
                                        new GameFileWithCopiesReadModel(
                                                TestGameFileReadModel.from(TestGameFile.gog()),
                                                List.of(
                                                        TestFileCopyReadModel.from(TestFileCopy.tracked()),
                                                        TestFileCopyReadModel.from(TestFileCopy.inProgressBuilder()
                                                                .id(IN_PROGRESS_FILE_COPY_ID)
                                                                .build()),
                                                        TestFileCopyReadModel.from(TestFileCopy.failedWithoutFilePath())
                                                )
                                        )
                                ))
                                .build(),
                        List.of(TestFileCopyReplicationProgress.twentyFivePercentBuilder()
                                .withFileCopyId(IN_PROGRESS_FILE_COPY_ID)
                                .build())
                )
        ), pagination);
        when(getGamesWithFilesUseCase.getGamesWithFiles(pagination, expectedFilter))
                .thenReturn(gameWithFileCopiesPage);

        mockMvc.perform(get("/api/games?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse()));
    }

    private String expectedJsonResponse() {
        return """
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
                                    "fileCopiesWithProgress": [
                                      {
                                          fileCopy: {
                                            "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                                            "naturalId": {
                                                "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                                "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                                            },
                                            "status": "TRACKED",
                                            "failedReason": null,
                                            "filePath": null,
                                            "dateCreated": "2022-04-29T14:15:53",
                                            "dateModified": "2023-04-29T14:15:53"
                                          },
                                          progress: null
                                      },
                                      {
                                          fileCopy: {
                                            "id": "d6c81f47-e2b6-424b-b997-0dad6aa372c7",
                                            "naturalId": {
                                                "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                                "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                                            },
                                            "status": "IN_PROGRESS",
                                            "failedReason": null,
                                            "filePath": "someFilePath",
                                            "dateCreated": "2022-04-29T14:15:53",
                                            "dateModified": "2023-04-29T14:15:53"
                                          },
                                          progress: {
                                            percentage: 25,
                                            timeLeftSeconds: 10
                                          }
                                      },
                                      {
                                          fileCopy: {
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
                                          },
                                          progress: null
                                      }
                                    ]
                                }
                            ]
                        }
                    ]
                }
                """;
    }

    @Test
    void shouldGetGamesWithFilters() throws Exception {
        var gameId = new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        var pagination = new Pagination(0, 2);
        var searchQuery = "someSearchQuery";
        var expectedFilter = new GameWithFileCopiesSearchFilter(searchQuery, FileCopyStatus.ENQUEUED);
        Page<GameWithFileCopiesAndReplicationProgresses> gameWithFileCopiesPage = TestPage.of(List.of(
                new GameWithFileCopiesAndReplicationProgresses(
                        TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                                .withId(gameId.toString())
                                .withTitle("Test Game")
                                .withGameFilesWithCopies(List.of(
                                        new GameFileWithCopiesReadModel(
                                                TestGameFileReadModel.from(TestGameFile.gog()),
                                                List.of(
                                                        TestFileCopyReadModel.from(TestFileCopy.tracked()),
                                                        TestFileCopyReadModel.from(TestFileCopy.inProgressBuilder()
                                                                .id(IN_PROGRESS_FILE_COPY_ID)
                                                                .build()),
                                                        TestFileCopyReadModel.from(TestFileCopy.failedWithoutFilePath())
                                                )
                                        )
                                ))
                                .build(),
                        List.of(TestFileCopyReplicationProgress.twentyFivePercentBuilder()
                                .withFileCopyId(IN_PROGRESS_FILE_COPY_ID)
                                .build())
                )
        ), pagination);
        when(getGamesWithFilesUseCase.getGamesWithFiles(pagination, expectedFilter))
                .thenReturn(gameWithFileCopiesPage);

        mockMvc.perform(get("/api/games?page=0&size=2&query=someSearchQuery&file-copy-status=ENQUEUED"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJsonResponse()));
    }
}