package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.domain.backup.services.EnqueuedFileBackupProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = FileBackupQueueSpringScheduler.class,
        properties = "file-download-queue-scheduler.rate-ms=1")
@EnableScheduling
class FileBackupQueueSpringSchedulerTest {

    @MockBean
    private EnqueuedFileBackupProcessor enqueuedFileBackupProcessor;

    @Test
    void shouldProcessQueueOnSchedule() {
        await()
                .pollInterval(Duration.ofMillis(5))
                .atMost(Duration.ofMillis(100))
                .untilAsserted(() -> verify(enqueuedFileBackupProcessor, atLeastOnce()).processQueue());
    }
}