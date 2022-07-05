package dev.codesoapbox.backity.core.files.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.discovery.domain.services.FileDiscoveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileDiscoveryController.class)
class FileDiscoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileDiscoveryService fileDiscoveryService;

    @MockBean
    private DiscoveredFileRepository repository;

    @Test
    void shouldGetDiscoveredFiles() throws Exception {
        DiscoveredFile discoveredFile = DiscoveredFile.builder()
                .id(new DiscoveredFileId("someUrl", "someVersion"))
                .uniqueId(UUID.fromString("780677cc-1e8c-43a3-ba70-a4ee3fec8555"))
                .build();

        Pageable pageable = Pageable.ofSize(1);
        when(repository.findAllUnqueued(pageable))
                .thenReturn(new PageImpl<>(singletonList(discoveredFile), pageable, 2));

        var expectedJson = """
                {
                  "content": [
                    {
                      "id": "780677cc-1e8c-43a3-ba70-a4ee3fec8555",
                      "url": "someUrl",
                      "version": "someVersion",
                      "source": null,
                      "name": null,
                      "gameTitle": null,
                      "size": null,
                      "dateCreated": null,
                      "dateModified": null,
                      "enqueued": false,
                      "ignored": false
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