package dev.codesoapbox.backity.core.files.downloading.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static dev.codesoapbox.backity.testing.assertions.SimpMessagingAssertions.assertSendsMessage;

@ExtendWith(MockitoExtension.class)
class FileDownloadSpringMessageServiceTest {

    @InjectMocks
    private FileDownloadSpringMessageService fileDownloadSpringMessageService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    void sendDownloadStarted() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "id": 123,
                  "source": "someSource",
                  "url": "someUrl",
                  "name": "someName",
                  "gameTitle": "someGameTitle",
                  "version": "someVersion",
                  "size": "someSize",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "status": "IN_PROGRESS",
                  "failedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDownloadMessageTopics.DOWNLOAD_STARTED.toString(),
                () -> fileDownloadSpringMessageService.sendDownloadStarted(
                        new EnqueuedFileDownload(
                                123L,
                                "someSource",
                                "someUrl",
                                "someName",
                                "someGameTitle",
                                "someVersion",
                                "someSize",
                                LocalDateTime.MIN,
                                DownloadStatus.IN_PROGRESS,
                                "someFailedReason"
                        )));
    }

    @Test
    void sendDownloadFinished() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "id": 123,
                  "source": "someSource",
                  "url": "someUrl",
                  "name": "someName",
                  "gameTitle": "someGameTitle",
                  "version": "someVersion",
                  "size": "someSize",
                  "dateCreated": "-999999999-01-01T00:00:00",
                  "status": "IN_PROGRESS",
                  "failedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(),
                () -> fileDownloadSpringMessageService.sendDownloadFinished(
                        new EnqueuedFileDownload(
                                123L,
                                "someSource",
                                "someUrl",
                                "someName",
                                "someGameTitle",
                                "someVersion",
                                "someSize",
                                LocalDateTime.MIN,
                                DownloadStatus.IN_PROGRESS,
                                "someFailedReason"
                        )));
    }
}