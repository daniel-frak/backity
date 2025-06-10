package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

public final class TestGameContentDiscoveryWsEvent {

    private static final String GAME_PROVIDER_ID = "TestGameProviderId";

    public static GameContentDiscoveryProgressChangedWsEvent progressChanged() {
        return new GameContentDiscoveryProgressChangedWsEvent(
                GAME_PROVIDER_ID,
                50,
                999,
                5,
                70
        );
    }

    public static GameContentDiscoveryStartedWsEvent discoveryStarted() {
        return new GameContentDiscoveryStartedWsEvent(
                GAME_PROVIDER_ID
        );
    }
}