package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http;

import dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers.GameFileDetailsRestResource;
import dev.codesoapbox.backity.core.gamefiledetails.application.GetCurrentlyDownloadingFileUseCase;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.fullFileDetails;
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
        GameFileDetails gameFileDetails = fullFileDetails().build();

        when(useCase.findCurrentlyDownloadingFile())
                .thenReturn(Optional.of(gameFileDetails));

        var expectedJson = """
                {
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
                    }""";

        mockMvc.perform(get("/api/" + GameFileDetailsRestResource.RESOURCE_URL + "/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}