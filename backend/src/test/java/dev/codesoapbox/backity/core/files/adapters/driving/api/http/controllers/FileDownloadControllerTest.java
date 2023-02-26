package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.domain.downloading.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import dev.codesoapbox.backity.core.files.domain.downloading.services.FileDownloadQueue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(FileDownloadController.class)
class FileDownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameFileVersionRepository gameFileVersionRepository;

    @MockBean
    private FileDownloadQueue fileDownloadQueue;

    @Test
    void shouldGetCurrentlyDownloading() throws Exception {
        GameFileVersion gameFileVersion = GameFileVersion.builder()
                .id(1L)
                .source("someSource")
                .url("someUrl")
                .name("someName")
                .gameTitle("someGameTitle")
                .version("someVersion")
                .size("someSize")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(DownloadStatus.FAILED)
                .failedReason("someFailedReason")
                .build();

        when(gameFileVersionRepository.findCurrentlyDownloading())
                .thenReturn(Optional.of(gameFileVersion));

        var expectedJson = """
                {
                  "id": 1,
                  "source": "someSource",
                  "url": "someUrl",
                  "name": "someName",
                  "gameTitle": "someGameTitle",
                  "version": "someVersion",
                  "size": "someSize",
                  "dateCreated": "2022-04-29T14:15:53",
                  "status": "FAILED",
                  "failedReason": "someFailedReason"
                }""";

        mockMvc.perform(get("/api/downloads/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetQueueItems() throws Exception {
        GameFileVersion gameFileVersion = GameFileVersion.builder()
                .id(1L)
                .source("someSource")
                .url("someUrl")
                .name("someName")
                .gameTitle("someGameTitle")
                .version("someVersion")
                .size("someSize")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(DownloadStatus.WAITING)
                .build();

        Pageable pageable = Pageable.ofSize(1);
        when(gameFileVersionRepository.findAllWaitingForDownload(pageable))
                .thenReturn(new PageImpl<>(singletonList(gameFileVersion), pageable, 2));

        var expectedJson = """
                {
                  "content": [
                    {
                      "id": 1,
                      "source": "someSource",
                      "url": "someUrl",
                      "name": "someName",
                      "gameTitle": "someGameTitle",
                      "version": "someVersion",
                      "size": "someSize",
                      "dateCreated": "2022-04-29T14:15:53",
                      "status": "WAITING",
                      "failedReason": null
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
                    "paged": true,
                    "unpaged": false
                  },
                  "totalElements": 2,
                  "totalPages": 2,
                  "last": false,
                  "size": 1,
                  "number": 0,
                  "sort": {
                    "empty": true,
                    "sorted": false,
                    "unsorted": true
                  },
                  "numberOfElements": 1,
                  "first": true,
                  "empty": false
                }""";

        mockMvc.perform(get("/api/downloads/queue?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetProcessedFiles() throws Exception {
        GameFileVersion gameFileVersion = GameFileVersion.builder()
                .id(1L)
                .source("someSource")
                .url("someUrl")
                .name("someName")
                .gameTitle("someGameTitle")
                .version("someVersion")
                .size("someSize")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(DownloadStatus.DOWNLOADED)
                .build();

        Pageable pageable = Pageable.ofSize(1);
        when(gameFileVersionRepository.findAllProcessed(pageable))
                .thenReturn(new PageImpl<>(singletonList(gameFileVersion), pageable, 2));

        var expectedJson = """
                {
                   "content": [
                     {
                       "id": 1,
                       "source": "someSource",
                       "url": "someUrl",
                       "name": "someName",
                       "gameTitle": "someGameTitle",
                       "version": "someVersion",
                       "size": "someSize",
                       "dateCreated": "2022-04-29T14:15:53",
                       "status": "DOWNLOADED",
                       "failedReason": null
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
                   "last": false,
                   "totalPages": 2,
                   "totalElements": 2,
                   "size": 1,
                   "number": 0,
                   "sort": {
                     "empty": true,
                     "sorted": false,
                     "unsorted": true
                   },
                   "first": true,
                   "numberOfElements": 1,
                   "empty": false
                 }""";

        mockMvc.perform(get("/api/downloads/processed?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldDownload() throws Exception {
        Long id = 1L;

        GameFileVersion gameFileVersion = GameFileVersion.builder()
                .id(id)
                .build();

        when(gameFileVersionRepository.findById(id))
                .thenReturn(Optional.of(gameFileVersion));

        mockMvc.perform(get("/api/downloads/enqueue/" + id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(fileDownloadQueue).enqueue(gameFileVersion);
    }

    @Test
    void shouldNotDownloadWhenFileNotFound(CapturedOutput capturedOutput) throws Exception {
        Long id = 123L;

        when(gameFileVersionRepository.findById(id))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/downloads/enqueue/" + id))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(fileDownloadQueue, never()).enqueue(any());
        assertTrue(capturedOutput.getOut().contains(
                "Could not enqueue file. Game file version not found: 123"));
    }
}