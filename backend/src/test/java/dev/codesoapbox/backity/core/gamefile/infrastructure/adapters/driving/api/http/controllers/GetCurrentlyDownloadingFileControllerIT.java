package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.application.usecases.GetCurrentlyDownloadingFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers.FileCopiesRestResource;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@ControllerTest
class GetCurrentlyDownloadingFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetCurrentlyDownloadingFileCopyUseCase useCase;

    @Test
    void shouldGetCurrentlyDownloadingFile() throws Exception {
        FileCopy fileCopy = TestFileCopy.inProgress();
        GameFile gameFile = TestGameFile.gog();
        Game game = TestGame.any();
        FileCopyWithContext fileCopyWithContext = new FileCopyWithContext(fileCopy, gameFile, game);

        when(useCase.findCurrentlyDownloadingFileCopy())
                .thenReturn(Optional.of(fileCopyWithContext));

        var expectedJson = """
                {
                  fileCopy: {
                    "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                    "naturalId": {
                        "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                        "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                    },
                    "status": "IN_PROGRESS",
                    "failedReason": null,
                    "filePath": "someFilePath"
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
                  }
                }
                """;

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL + "/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}