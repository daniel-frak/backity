package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.schedule;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class GogAuthRefreshSpringScheduler {

    private final GogAuthService gogAuthService;

    @Scheduled(fixedRateString = "${backity.gog-auth-scheduler.rate-ms}")
    public void refreshAccessTokenIfNeeded() {
        gogAuthService.refreshAccessTokenIfNeeded();
    }
}