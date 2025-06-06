package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;

import java.time.Duration;

public final class TestGameContentDiscoveryEvent {

    private static final GameProviderId GAME_PROVIDER_ID = new GameProviderId("TestGameProviderId");

    public static GameFileDiscoveredEvent fileDiscovered() {
        return new GameFileDiscoveredEvent(
                "Original game title",
                "originalFileName",
                "fileTitle",
                new FileSize(5120L)
        );
    }

    public static GameContentDiscoveryProgressChangedEvent progressChanged() {
        return new GameContentDiscoveryProgressChangedEvent(
                GAME_PROVIDER_ID,
                50,
                Duration.ofSeconds(999)
        );
    }

    public static GameContentDiscoveryStatusChangedEvent statusChangedToInProgress() {
        return new GameContentDiscoveryStatusChangedEvent(
                GAME_PROVIDER_ID,
                true
        );
    }

    public static GameContentDiscoveryStatusChangedEvent statusChangedToStopped() {
        return new GameContentDiscoveryStatusChangedEvent(
                GAME_PROVIDER_ID,
                false
        );
    }
}