package dev.codesoapbox.backity.core.discovery.domain.events;

public class TestFileDiscoveryEvents {

    private static final String GAME_PROVIDER_ID = "TestGameProviderId";

    public static FileDiscoveredEvent discovered() {
        return new FileDiscoveredEvent(
                "Original game title",
                "originalFileName",
                "fileTitle",
                "5 KB"
        );
    }

    public static FileDiscoveryProgressChangedEvent progressChanged() {
        return new FileDiscoveryProgressChangedEvent(
                GAME_PROVIDER_ID,
                50,
                999
        );
    }

    public static FileDiscoveryStatusChangedEvent statusChanged() {
        return new FileDiscoveryStatusChangedEvent(
                GAME_PROVIDER_ID,
                true
        );
    }
}