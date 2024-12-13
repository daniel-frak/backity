package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthSpringService;
import dev.codesoapbox.backity.testing.TemporaryMockBean;
import org.junit.jupiter.api.Test;
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

    @TemporaryMockBean
    private GogAuthSpringService gogAuthSpringService;

    @Test
    void shouldRefreshAccessTokenIfNeeded() {
        await()
                .pollInterval(Duration.ofMillis(5))
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(gogAuthSpringService, atLeastOnce()).refreshAccessTokenIfNeeded());
    }
}