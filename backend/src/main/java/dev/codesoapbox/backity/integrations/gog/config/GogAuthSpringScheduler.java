package dev.codesoapbox.backity.integrations.gog.config;


import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthSpringService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class GogAuthSpringScheduler {

    private final GogAuthSpringService gogAuthSpringService;

    @Scheduled(fixedRateString = "${gog-auth-scheduler.rate-ms}")
    public void refreshAccessTokenIfNeeded() {
        gogAuthSpringService.refreshAccessTokenIfNeeded();
    }
}