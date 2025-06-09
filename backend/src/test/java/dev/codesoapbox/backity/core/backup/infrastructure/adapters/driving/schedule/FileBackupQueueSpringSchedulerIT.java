package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driving.schedule;

import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestFileCopyUseCase;
import dev.codesoapbox.backity.core.backup.infrastructure.config.FileBackupQueueSchedulerBeanConfig;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@EnableScheduling
@SpringJUnitConfig(FileBackupQueueSchedulerBeanConfig.class)
@TestPropertySource(properties = "backity.file-download-queue-scheduler.rate-ms=1")
class FileBackupQueueSpringSchedulerIT {

    @MockitoBean
    private BackUpOldestFileCopyUseCase backUpOldestFileCopyUseCase;

    @Test
    void shouldProcessQueueOnSchedule() {
        await()
                .pollInterval(Duration.ofMillis(5))
                .atMost(Duration.ofMillis(100))
                .untilAsserted(() -> verify(backUpOldestFileCopyUseCase,
                        atLeast(2)) // First time always happens on startup
                        .backUpOldestFileCopy());
    }
}