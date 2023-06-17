package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.config.gamefiledetails.GameFileDetailsJsonBeanConfig;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.config.jpa.SharedControllerBeanConfig;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.discovered;
import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.full;
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
@Import({SharedControllerBeanConfig.class, GameFileDetailsJsonBeanConfig.class})
class FileBackupControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameFileDetailsRepository gameFileDetailsRepository;

    @Test
    void shouldGetCurrentlyDownloading() throws Exception {
        GameFileDetails gameFileDetails = full().build();

        when(gameFileDetailsRepository.findCurrentlyDownloading())
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

        mockMvc.perform(get("/api/backups/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetQueueItems() throws Exception {
        GameFileDetails gameFileDetails = full().build();

        var pagination = new Pagination(0, 1);
        when(gameFileDetailsRepository.findAllWaitingForDownload(pagination))
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

        mockMvc.perform(get("/api/backups/queue?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetProcessedFiles() throws Exception {
        GameFileDetails gameFileDetails = full().build();

        var pagination = new Pagination(0, 1);
        when(gameFileDetailsRepository.findAllProcessed(pagination))
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

        mockMvc.perform(get("/api/backups/processed?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldEnqueueForDownload() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var uuid = UUID.fromString(stringUuid);

        GameFileDetails gameFileDetails = discovered().build();

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
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";

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