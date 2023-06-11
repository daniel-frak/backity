package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.domain.discovery.services.FileDiscoveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileDiscoveryController.class)
class FileDiscoveryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileDiscoveryService fileDiscoveryService;

    @MockBean
    private GameFileDetailsRepository repository;

    @Test
    void shouldGetDiscoveredFiles() throws Exception {
        var gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        Pageable pageable = Pageable.ofSize(1);
        when(repository.findAllDiscovered(pageable))
                .thenReturn(new PageImpl<>(singletonList(gameFileDetails), pageable, 2));

        var expectedJson = """
                {
                  "content": [
                    {
                      "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                      "gameId": "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                      "sourceFileDetails": {
                        "sourceId": "someSourceId1",
                        "originalGameTitle": "someOriginalGameTitle1",
                        "fileTitle": "someFileTitle1",
                        "version": "someVersion1",
                        "url": "someUrl1",
                        "originalFileName": "someOriginalFileName1",
                        "size": "5 KB"
                      },
                      "backupDetails": {
                        "status": "ENQUEUED",
                        "failedReason": null,
                        "filePath": null
                      },
                      "dateCreated": "2022-04-29T14:15:53",
                      "dateModified": "2023-04-29T14:15:53"
                    }
                  ],
                  "pageable": {
                     "sort": {
                       "empty": true,
                       "sorted": false,
                       "unsorted": true
                     },
                     "offset": 0,
                     "pageNumber": 0,
                     "pageSize": 1,
                     "unpaged": false,
                     "paged": true
                   },
                  "totalPages": 2,
                  "totalElements": 2,
                  "last": false,
                  "size": 1,
                  "number": 0,
                  "sort": {
                    "empty": true,
                    "unsorted": true,
                    "sorted": false
                  },
                  "first": true,
                  "numberOfElements": 1,
                  "empty": false
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
        FileDiscoveryStatus status = new FileDiscoveryStatus("someSource", true);

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