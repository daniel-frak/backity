package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryResult;

import java.time.Duration;

public final class TestGameContentDiscoveryEvent {

    private static final GameProviderId GAME_PROVIDER_ID = new GameProviderId("TestGameProviderId");

    public static GameContentDiscoveryProgressChangedEvent progressChanged() {
        return new GameContentDiscoveryProgressChangedEvent(
                GAME_PROVIDER_ID,
                50,
                Duration.ofSeconds(999),
                5,
                70
        );
    }

    public static GameContentDiscoveryStartedEvent discoveryStarted() {
        return new GameContentDiscoveryStartedEvent(GAME_PROVIDER_ID);
    }

    public static GameContentDiscoveryStoppedEvent discoveryStopped() {
        return new GameContentDiscoveryStoppedEvent(
                GAME_PROVIDER_ID,
                TestGameContentDiscoveryResult.gog()
        );
    }
}