package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
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

import java.util.Optional;
import java.util.UUID;

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
        GameFileDetails gameFileDetails = TestGameFileDetails.FULL_GAME_FILE_DETAILS.get();

        when(gameFileDetailsRepository.findCurrentlyDownloading())
                .thenReturn(Optional.of(gameFileDetails));

        var expectedJson = """
                {
                      "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                      "gameId": "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
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

        mockMvc.perform(get("/api/backups/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetQueueItems() throws Exception {
        GameFileDetails gameFileDetails = TestGameFileDetails.FULL_GAME_FILE_DETAILS.get();

        Pageable pageable = Pageable.ofSize(1);
        when(gameFileDetailsRepository.findAllWaitingForDownload(pageable))
                .thenReturn(new PageImpl<>(singletonList(gameFileDetails), pageable, 2));

        var expectedJson = """
                {
                  "content": [
                     {
                      "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                      "gameId": "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
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
        GameFileDetails gameFileDetails = TestGameFileDetails.FULL_GAME_FILE_DETAILS.get();

        Pageable pageable = Pageable.ofSize(1);
        when(gameFileDetailsRepository.findAllProcessed(pageable))
                .thenReturn(new PageImpl<>(singletonList(gameFileDetails), pageable, 2));

        var expectedJson = """
                {
                   "content": [
                     {
                      "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                      "gameId": "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
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
        String stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        UUID uuid = UUID.fromString(stringUuid);

        GameFileDetails gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        when(gameFileDetailsRepository.findById(new GameFileDetailsId(uuid)))
                .thenReturn(Optional.of(gameFileDetails));

        mockMvc.perform(get("/api/backups/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(FileBackupStatus.ENQUEUED, gameFileDetails.getBackupDetails().getStatus());
        verify(gameFileDetailsRepository).save(gameFileDetails);
    }

    @Test
    void shouldNotDownloadWhenFileNotFound(CapturedOutput capturedOutput) throws Exception {
        String stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";

        when(gameFileDetailsRepository.findById(new GameFileDetailsId(UUID.fromString(stringUuid))))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/backups/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(gameFileDetailsRepository, never()).save(any());
        assertTrue(capturedOutput.getOut().contains(
                "Could not enqueue file. Game file version not found: acde26d7-33c7-42ee-be16-bca91a604b48"));
    }
}