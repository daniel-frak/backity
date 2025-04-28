package dev.codesoapbox.backity.core.backup.config;

import dev.codesoapbox.backity.core.backup.application.usecases.BackUpOldestGameFileUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@EnableScheduling
@SpringJUnitConfig(FileBackupQueueSchedulerBeanConfig.class)
@TestPropertySource(properties = "backity.file-download-queue-scheduler.rate-ms=1")
class FileBackupQueueSpringSchedulerIT {

    @MockitoBean
    private BackUpOldestGameFileUseCase backUpOldestGameFileUseCase;

    @Test
    void shouldProcessQueueOnSchedule() {
        await()
                .pollInterval(Duration.ofMillis(5))
                .atMost(Duration.ofMillis(100))
                .untilAsserted(() -> verify(backUpOldestGameFileUseCase,
                        atLeast(2)) // First time always happens on startup
                        .backUpOldestGameFile());
    }
}