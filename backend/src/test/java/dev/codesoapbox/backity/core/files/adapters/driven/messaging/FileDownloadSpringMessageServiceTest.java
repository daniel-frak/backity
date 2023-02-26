package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.model.messages.FileDownloadProgress;
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
    void shouldSendDownloadStarted() throws JsonProcessingException {
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
                  "dateModified":"+999999999-12-31T23:59:59.999999999",
                  "status": "DOWNLOAD_IN_PROGRESS",
                  "failedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDownloadMessageTopics.DOWNLOAD_STARTED.toString(),
                () -> fileDownloadSpringMessageService.sendDownloadStarted(
                        new GameFileVersion(
                                123L,
                                "someSource",
                                "someUrl",
                                "someName",
                                "someFilePath",
                                "someGameTitle",
                                "someVersion",
                                "someSize",
                                LocalDateTime.MIN,
                                LocalDateTime.MAX,
                                FileStatus.DOWNLOAD_IN_PROGRESS,
                                "someFailedReason"
                        )));
    }

    @Test
    void shouldSendDownloadProgress() throws JsonProcessingException {
        var expectedPayload = """
                {
                  "percentage": 25,
                  "timeLeftSeconds": 1234
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDownloadMessageTopics.DOWNLOAD_PROGRESS.toString(),
                () -> fileDownloadSpringMessageService.sendProgress(
                        new FileDownloadProgress(
                                25,
                                1234L
                        )));
    }

    @Test
    void shouldSendDownloadFinished() throws JsonProcessingException {
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
                  "dateModified":"+999999999-12-31T23:59:59.999999999",
                  "status": "DOWNLOAD_IN_PROGRESS",
                  "failedReason": "someFailedReason"
                }
                """;

        assertSendsMessage(simpMessagingTemplate, expectedPayload,
                FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(),
                () -> fileDownloadSpringMessageService.sendDownloadFinished(
                        new GameFileVersion(
                                123L,
                                "someSource",
                                "someUrl",
                                "someName",
                                "someFilePath",
                                "someGameTitle",
                                "someVersion",
                                "someSize",
                                LocalDateTime.MIN,
                                LocalDateTime.MAX,
                                FileStatus.DOWNLOAD_IN_PROGRESS,
                                "someFailedReason"
                        )));
    }
}