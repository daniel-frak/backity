package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.GetDiscoveredFileListUseCase;
import dev.codesoapbox.backity.core.gamefile.application.GetEnqueuedFileListUseCase;
import dev.codesoapbox.backity.core.gamefile.application.GetProcessedFileListUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
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
class GetGameFileListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetDiscoveredFileListUseCase getDiscoveredFilesUseCase;

    @Autowired
    private GetEnqueuedFileListUseCase getEnqueuedFilesUseCase;

    @Autowired
    private GetProcessedFileListUseCase getProcessedFilesUseCase;

    @Test
    void shouldGetDiscoveredFileList() throws Exception {
        var pagination = new Pagination(0, 1);
        mockDiscoveredFileExists(pagination);
        var fileStatus = "DISCOVERED";
        String filePath = "null";
        var expectedJson = getExpectedJson(fileStatus, filePath);

        mockMvc.perform(get("/api/" + GameFileRestResource.RESOURCE_URL
                            + "?processing-status=discovered&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private void mockDiscoveredFileExists(Pagination pagination) {
        GameFile gameFile = TestGameFile.discovered();
        when(getDiscoveredFilesUseCase.getDiscoveredFileList(pagination))
                .thenReturn(pageWith(gameFile));
    }

    private Page<GameFile> pageWith(GameFile gameFile) {
        return new Page<>(singletonList(gameFile),
                4, 3, 2, 1, 0);
    }

    private String getExpectedJson(String status, String filePath) {
        return String.format("""
                {
                  "content": [
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
                        "status": "%s",
                        "failedReason": null,
                        "filePath": %s
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
                }""", status, filePath);
    }

    @Test
    void shouldGetEnqueuedFileList() throws Exception {
        var pagination = new Pagination(0, 1);
        mockEnqueuedFileExists(pagination);
        var fileStatus = "ENQUEUED";
        String filePath = "null";
        var expectedJson = getExpectedJson(fileStatus, filePath);

        mockMvc.perform(get("/api/" + GameFileRestResource.RESOURCE_URL
                            + "?processing-status=enqueued&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private void mockEnqueuedFileExists(Pagination pagination) {
        GameFile gameFile = TestGameFile.enqueued();
        when(getEnqueuedFilesUseCase.getEnqueuedFileList(pagination))
                .thenReturn(pageWith(gameFile));
    }

    @Test
    void shouldGetProcessedFileList() throws Exception {
        var pagination = new Pagination(0, 1);
        mockProcessedFileExists(pagination);
        var fileStatus = "SUCCESS";
        String filePath = "someFilePath";
        var expectedJson = getExpectedJson(fileStatus, filePath);

        mockMvc.perform(get("/api/" + GameFileRestResource.RESOURCE_URL
                            + "?processing-status=processed&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private void mockProcessedFileExists(Pagination pagination) {
        GameFile gameFile = TestGameFile.successful();
        when(getProcessedFilesUseCase.getProcessedFileList(pagination))
                .thenReturn(pageWith(gameFile));
    }
}