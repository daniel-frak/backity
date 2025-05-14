package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

public final class TestGameContentDiscoveryWsEvent {

    private static final String GAME_PROVIDER_ID = "TestGameProviderId";

    public static FileDiscoveredWsEvent fileDiscovered() {
        return new FileDiscoveredWsEvent(
                "Original game title",
                "originalFileName",
                "fileTitle",
                "5 KB"
        );
    }

    public static GameContentDiscoveryProgressChangedWsEvent progressChanged() {
        return new GameContentDiscoveryProgressChangedWsEvent(
                GAME_PROVIDER_ID,
                50,
                999
        );
    }

    public static GameContentDiscoveryStatusChangedWsEvent statusChanged() {
        return new GameContentDiscoveryStatusChangedWsEvent(
                GAME_PROVIDER_ID,
                true
        );
    }
}