package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backup.domain.TestFileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.application.usecases.GetFileCopyQueueUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@ControllerTest
class GetFileCopyQueueControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetFileCopyQueueUseCase getFileCopyQueueUseCase;

    @Test
    void shouldGetQueue() throws Exception {
        var expectedPagination = new Pagination(0, 1);
        mockEnqueuedFileExists(expectedPagination);
        var expectedJson = """
                {
                  "content": [
                    {
                      fileCopy: {
                        "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                        "naturalId": {
                            "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                            "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                        },
                        "status": "ENQUEUED",
                        "failedReason": null,
                        "filePath": null
                      },
                      gameFile: {
                          "fileSource": {
                            "gameProviderId": "GOG",
                            "originalGameTitle": "Game 1",
                            "fileTitle": "Game 1 (Installer)",
                            "version": "1.0.0",
                            "url": "/downlink/some_game/some_file",
                            "originalFileName": "game_1_installer.exe",
                            "size": "5 KB"
                          }
                      },
                      game: {
                        title: "Test Game"
                      },
                      backupTarget: {
                        name: "Local folder",
                        "storageSolutionId": "storageSolution1"
                      },
                      progress: {
                        percentage: 25,
                        timeLeftSeconds: 10
                      }
                    }
                  ],
                  "totalPages": 3,
                  "totalElements": 2,
                  "pagination": {
                      "size": 1,
                      "page": 0
                  }
                }
                """;

        mockMvc.perform(get("/api/" + FileCopyQueueRestResource.RESOURCE_URL + "?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private void mockEnqueuedFileExists(Pagination expectedPagination) {
        var fileCopyWithContext = new FileCopyWithContext(
                TestFileCopy.enqueued(),
                TestGameFile.gog(),
                TestGame.any(),
                TestBackupTarget.localFolder(),
                TestFileCopyReplicationProgress.twentyFivePercent()
        );
        when(getFileCopyQueueUseCase.getFileCopyQueue(expectedPagination))
                .thenReturn(pageWith(fileCopyWithContext));
    }

    private Page<FileCopyWithContext> pageWith(FileCopyWithContext fileCopyWithContext) {
        return new Page<>(singletonList(fileCopyWithContext),
                3, 2, new Pagination(0, 1));
    }

    @Test
    void shouldReturnBadRequestGivenSizeIsMissingFromUrl() throws Exception {
        var expectedJson = """
                [{
                  "fieldName":"size",
                  "message":"must not be null"
                }]
                """;

        mockMvc.perform(get("/api/" + FileCopyQueueRestResource.RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedJson));
    }
}