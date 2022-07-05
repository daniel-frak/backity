package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth.GogAuthSpringService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = GogAuthSpringScheduler.class,
        properties = "gog-auth-scheduler.rate-ms=1")
@EnableScheduling
class GogAuthSpringSchedulerTest {

    @MockBean
    private GogAuthSpringService gogAuthSpringService;

    @Test
    void shouldRefreshAccessTokenIfNeeded() {
        await()
                .pollInterval(Duration.ofMillis(5))
                .atMost(Duration.ofMillis(10))
                .untilAsserted(() -> verify(gogAuthSpringService, atLeastOnce()).refreshAccessTokenIfNeeded());
    }
}