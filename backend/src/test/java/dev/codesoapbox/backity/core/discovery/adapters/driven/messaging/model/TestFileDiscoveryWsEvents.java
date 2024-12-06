package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

public class TestFileDiscoveryWsEvents {

    private static final String GAME_PROVIDER_ID = "TestGameProviderId";

    public static FileDiscoveredWsEvent discovered() {
        return new FileDiscoveredWsEvent(
                "Original game title",
                "originalFileName",
                "fileTitle",
                "5 KB"
        );
    }

    public static FileDiscoveryProgressChangedWsEvent progressChanged() {
        return new FileDiscoveryProgressChangedWsEvent(
                GAME_PROVIDER_ID,
                50,
                999
        );
    }

    public static FileDiscoveryStatusChangedWsEvent statusChanged() {
        return new FileDiscoveryStatusChangedWsEvent(
                GAME_PROVIDER_ID,
                true
        );
    }
}