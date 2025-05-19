package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.usecases.GetCurrentlyDownloadingFileUseCase;
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
    private GetCurrentlyDownloadingFileUseCase useCase;

    @Test
    void shouldGetCurrentlyDownloadingFile() throws Exception {
        GameFile gameFile = TestGameFile.inProgress();

        when(useCase.findCurrentlyDownloadingFile())
                .thenReturn(Optional.of(gameFile));

        var expectedJson = """
                {
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
                      "fileBackup": {
                        "status": "IN_PROGRESS",
                        "failedReason": null,
                        "filePath": "someFilePath"
                      },
                      "dateCreated": "2022-04-29T14:15:53",
                      "dateModified": "2023-04-29T14:15:53"
                    }""";

        mockMvc.perform(get("/api/" + GameFileRestResource.RESOURCE_URL + "/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}