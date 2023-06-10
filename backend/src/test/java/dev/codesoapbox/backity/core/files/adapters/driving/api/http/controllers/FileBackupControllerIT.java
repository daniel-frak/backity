package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(FileBackupController.class)
class FileBackupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameFileDetailsRepository gameFileDetailsRepository;

    @Test
    void shouldGetCurrentlyDownloading() throws Exception {
        var gameFileVersionBackup = new GameFileDetails(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, "someGameTitle", "someGameId", "someVersion", "100 KB",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.IN_PROGRESS, "someFailedReason");

        when(gameFileDetailsRepository.findCurrentlyDownloading())
                .thenReturn(Optional.of(gameFileVersionBackup));

        var expectedJson = """
                {
                  "id": 1,
                  "source": "someSource",
                  "url": "someUrl",
                  "title": "someTitle",
                  "gameTitle": "someGameTitle",
                  "version": "someVersion",
                  "size": "100 KB",
                  "dateCreated": "2022-04-29T14:15:53",
                  "backupStatus": "IN_PROGRESS",
                  "backupFailedReason": "someFailedReason"
                }""";

        mockMvc.perform(get("/api/backups/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetQueueItems() throws Exception {
        var gameFileVersionBackup = new GameFileDetails(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, "someGameTitle", "someGameId", "someVersion", "100 KB",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.ENQUEUED, null);

        Pageable pageable = Pageable.ofSize(1);
        when(gameFileDetailsRepository.findAllWaitingForDownload(pageable))
                .thenReturn(new PageImpl<>(singletonList(gameFileVersionBackup), pageable, 2));

        var expectedJson = """
                {
                  "content": [
                    {
                      "id": 1,
                      "source": "someSource",
                      "url": "someUrl",
                      "title": "someTitle",
                      "gameTitle": "someGameTitle",
                      "version": "someVersion",
                      "size": "100 KB",
                      "dateCreated": "2022-04-29T14:15:53",
                      "backupStatus": "ENQUEUED",
                      "backupFailedReason": null
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

        mockMvc.perform(get("/api/backups/queue?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetProcessedFiles() throws Exception {
        var gameFileVersionBackup = new GameFileDetails(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, "someGameTitle", "someGameId", "someVersion", "100 KB",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.SUCCESS, null);

        Pageable pageable = Pageable.ofSize(1);
        when(gameFileDetailsRepository.findAllProcessed(pageable))
                .thenReturn(new PageImpl<>(singletonList(gameFileVersionBackup), pageable, 2));

        var expectedJson = """
                {
                   "content": [
                     {
                       "id": 1,
                       "source": "someSource",
                       "url": "someUrl",
                       "title": "someTitle",
                       "gameTitle": "someGameTitle",
                       "version": "someVersion",
                       "size": "100 KB",
                       "dateCreated": "2022-04-29T14:15:53",
                       "backupStatus": "SUCCESS",
                       "backupFailedReason": null
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

        mockMvc.perform(get("/api/backups/processed?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldEnqueueForDownload() throws Exception {
        Long id = 1L;

        var gameFileVersionBackup = new GameFileDetails(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB",
                null, null, FileBackupStatus.DISCOVERED, null);

        when(gameFileDetailsRepository.findById(id))
                .thenReturn(Optional.of(gameFileVersionBackup));

        mockMvc.perform(get("/api/backups/enqueue/" + id))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(FileBackupStatus.ENQUEUED, gameFileVersionBackup.getBackupStatus());
        verify(gameFileDetailsRepository).save(gameFileVersionBackup);
    }

    @Test
    void shouldNotDownloadWhenFileNotFound(CapturedOutput capturedOutput) throws Exception {
        Long id = 123L;

        when(gameFileDetailsRepository.findById(id))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/backups/enqueue/" + id))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(gameFileDetailsRepository, never()).save(any());
        assertTrue(capturedOutput.getOut().contains(
                "Could not enqueue file. Game file version not found: 123"));
    }
}