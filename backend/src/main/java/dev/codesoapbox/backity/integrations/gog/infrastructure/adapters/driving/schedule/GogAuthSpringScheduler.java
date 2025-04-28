package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.schedule;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class GogAuthSpringScheduler {

    private final GogAuthService gogAuthService;

    @Scheduled(fixedRateString = "${backity.gog-auth-scheduler.rate-ms}")
    public void refreshAccessTokenIfNeeded() {
        gogAuthService.refreshAccessTokenIfNeeded();
    }
}