package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http;

import dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers.GameFileDetailsRestResource;
import dev.codesoapbox.backity.core.gamefiledetails.application.GetEnqueuedFileListUseCase;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.fullFileDetails;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@ControllerTest
class GetEnqueuedFileListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetEnqueuedFileListUseCase useCase;

    @Test
    void shouldGetEnqueuedFileList() throws Exception {
        GameFileDetails gameFileDetails = fullFileDetails().build();

        var pagination = new Pagination(0, 1);
        when(useCase.getEnqueuedFileList(pagination))
                .thenReturn(new Page<>(singletonList(gameFileDetails),
                        4, 3, 2, 1, 0));

        var expectedJson = """
                {
                  "content": [
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
                    }
                  ],
                  "size": 4,
                  "totalPages": 3,
                  "totalElements": 2,
                  "pageSize": 1,
                  "pageNumber": 0
                }""";

        mockMvc.perform(get("/api/" + GameFileDetailsRestResource.RESOURCE_URL + "/queue?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}