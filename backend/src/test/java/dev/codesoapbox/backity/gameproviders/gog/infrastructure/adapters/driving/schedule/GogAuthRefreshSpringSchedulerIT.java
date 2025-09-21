package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.schedule;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.GogAuthSpringService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.config.GogAuthSchedulerBeanConfig;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@EnableScheduling
@SpringJUnitConfig(GogAuthSchedulerBeanConfig.class)
@TestPropertySource(properties = "backity.gog-auth-scheduler.rate-ms=1")
class GogAuthRefreshSpringSchedulerIT {

    @MockitoBean
    private GogAuthSpringService gogAuthSpringService;

    @Test
    void shouldRefreshAccessTokenIfNeeded() {
        await()
                .pollInterval(Duration.ofMillis(5))
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> verify(gogAuthSpringService,
                        atLeast(2)) // First time always happens on startup
                        .refreshAccessTokenIfNeeded());
    }
}