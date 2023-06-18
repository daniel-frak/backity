package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http;

import dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers.GameFileDetailsController;
import dev.codesoapbox.backity.core.gamefiledetails.config.GameFileDetailsControllerBeanConfig;
import dev.codesoapbox.backity.core.gamefiledetails.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsId;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
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

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.fullFileDetails;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(GameFileDetailsController.class)
@Import({SharedControllerBeanConfig.class, GameFileDetailsControllerBeanConfig.class})
class GameFileDetailsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameFileDetailsRepository gameFileDetailsRepository;

    @Test
    void shouldGetDiscoveredFiles() throws Exception {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        var pagination = new Pagination(0, 1);
        when(gameFileDetailsRepository.findAllDiscovered(pagination))
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

        mockMvc.perform(get("/api/game-file-details/discovered?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetCurrentlyDownloading() throws Exception {
        GameFileDetails gameFileDetails = fullFileDetails().build();

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

        mockMvc.perform(get("/api/game-file-details/current"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetQueueItems() throws Exception {
        GameFileDetails gameFileDetails = fullFileDetails().build();

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

        mockMvc.perform(get("/api/game-file-details/queue?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldGetProcessedFiles() throws Exception {
        GameFileDetails gameFileDetails = fullFileDetails().build();

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

        mockMvc.perform(get("/api/game-file-details/processed?size=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldEnqueueForDownload() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var uuid = UUID.fromString(stringUuid);

        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        when(gameFileDetailsRepository.findById(new GameFileDetailsId(uuid)))
                .thenReturn(Optional.of(gameFileDetails));

        mockMvc.perform(get("/api/game-file-details/enqueue/" + stringUuid))
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

        mockMvc.perform(get("/api/game-file-details/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(gameFileDetailsRepository, never()).save(any());
        assertTrue(capturedOutput.getOut().contains(
                "Could not enqueue file. Game file version not found: acde26d7-33c7-42ee-be16-bca91a604b48"));
    }
}