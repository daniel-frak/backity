package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.config.FileDiscoveryControllerBeanConfig;
import dev.codesoapbox.backity.core.files.config.gamefiledetails.GameFileDetailsControllerBeanConfig;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.domain.discovery.services.FileDiscoveryService;
import dev.codesoapbox.backity.core.shared.config.jpa.SharedControllerBeanConfig;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.discovered;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileDiscoveryController.class)
@Import({SharedControllerBeanConfig.class, GameFileDetailsControllerBeanConfig.class,
        FileDiscoveryControllerBeanConfig.class})
class FileDiscoveryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileDiscoveryService fileDiscoveryService;

    @MockBean
    private GameFileDetailsRepository repository;

    @Test
    void shouldGetDiscoveredFiles() throws Exception {
        GameFileDetails gameFileDetails = discovered().build();

        var pagination = new Pagination(0, 1);
        when(repository.findAllDiscovered(pagination))
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
                        "failedReason": null,
                        "filePath": null
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

        mockMvc.perform(get("/api/discovered-files?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldStartFileDiscovery() throws Exception {
        mockMvc.perform(get("/api/discovered-files/discover"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(fileDiscoveryService).startFileDiscovery();
    }

    @Test
    void shouldStopFileDiscovery() throws Exception {
        mockMvc.perform(get("/api/discovered-files/stop-discovery"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(fileDiscoveryService).stopFileDiscovery();
    }

    @Test
    void shouldGetStatuses() throws Exception {
        var status = new FileDiscoveryStatus("someSource", true);

        when(fileDiscoveryService.getStatuses())
                .thenReturn(singletonList(status));

        var expectedJson = """
                [{
                  "source": "someSource",
                  "inProgress": true
                }]""";

        mockMvc.perform(get("/api/discovered-files/statuses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}