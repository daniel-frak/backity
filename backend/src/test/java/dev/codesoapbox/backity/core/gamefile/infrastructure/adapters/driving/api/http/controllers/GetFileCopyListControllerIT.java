package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.GetEnqueuedFileCopiesUseCase;
import dev.codesoapbox.backity.core.filecopy.application.usecases.GetProcessedFileCopiesUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers.FileCopiesRestResource;
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
class GetFileCopyListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetEnqueuedFileCopiesUseCase getEnqueuedFilesUseCase;

    @Autowired
    private GetProcessedFileCopiesUseCase getProcessedFilesUseCase;

    @Test
    void shouldGetEnqueuedFileList() throws Exception {
        var pagination = new Pagination(0, 1);
        mockEnqueuedFileExists(pagination);
        var fileStatus = "ENQUEUED";
        String filePath = "null";
        var expectedJson = getExpectedJson(fileStatus, filePath);

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL
                            + "?processing-status=enqueued&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private void mockEnqueuedFileExists(Pagination pagination) {
        FileCopy fileCopy = TestFileCopy.enqueued();
        when(getEnqueuedFilesUseCase.getEnqueuedFileCopies(pagination))
                .thenReturn(pageWith(fileCopy));
    }

    private Page<FileCopy> pageWith(FileCopy fileCopy) {
        return new Page<>(singletonList(fileCopy),
                4, 3, 2, 1, 0);
    }

    private String getExpectedJson(String status, String filePath) {
        return String.format("""
                {
                  "content": [
                    {
                      "id": "6df888e8-90b9-4df5-a237-0cba422c0310",
                      "naturalId": {
                          "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                          "backupTargetId": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                      },
                      "status": "%s",
                      "failedReason": null,
                      "filePath": %s,
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
    void shouldGetProcessedFileList() throws Exception {
        var pagination = new Pagination(0, 1);
        mockProcessedFileExists(pagination);
        var fileStatus = "SUCCESS";
        String filePath = "someFilePath";
        var expectedJson = getExpectedJson(fileStatus, filePath);

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL
                            + "?processing-status=processed&size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    private void mockProcessedFileExists(Pagination pagination) {
        FileCopy fileCopy = TestFileCopy.successful();
        when(getProcessedFilesUseCase.getProcessedFileCopies(pagination))
                .thenReturn(pageWith(fileCopy));
    }
}