package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.GetCurrentlyDownloadingFileUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.fullGameFile;
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
        GameFile gameFile = fullGameFile().build();

        when(useCase.findCurrentlyDownloadingFile())
                .thenReturn(Optional.of(gameFile));

        var expectedJson = """
                {
                      "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                      "gameId": "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                      "gameProviderFile": {
                        "gameProviderId": "someGameProviderId",
                        "originalGameTitle": "someOriginalGameTitle",
                        "fileTitle": "someFileTitle",
                        "version": "someVersion",
                        "url": "someUrl",
                        "originalFileName": "someOriginalFileName",
                        "size": "5 KB"
                      },
                      "fileBackup": {
                        "status": "DISCOVERED",
                        "failedReason": "someFailedReason",
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