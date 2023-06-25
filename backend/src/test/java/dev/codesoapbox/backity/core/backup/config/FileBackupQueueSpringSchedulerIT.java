package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.domain.EnqueuedFileBackupProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@EnableScheduling
@SpringJUnitConfig(FileBackupQueueSchedulerBeanConfig.class)
@TestPropertySource(properties = "file-download-queue-scheduler.rate-ms=1")
class FileBackupQueueSpringSchedulerIT {

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