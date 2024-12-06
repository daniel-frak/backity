package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthSpringService;
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
@SpringJUnitConfig(GogAuthSchedulerBeanConfig.class)
@TestPropertySource(properties = "gog-auth-scheduler.rate-ms=1")
class GogAuthSpringSchedulerIT {

    @MockBean
    private GogAuthSpringService gogAuthSpringService;

    @Test
    void shouldRefreshAccessTokenIfNeeded() {
        await()
                .pollInterval(Duration.ofMillis(5))
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(gogAuthSpringService, atLeastOnce()).refreshAccessTokenIfNeeded());
    }
}