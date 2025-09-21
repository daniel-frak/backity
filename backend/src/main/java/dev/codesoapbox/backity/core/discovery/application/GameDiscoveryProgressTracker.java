package dev.codesoapbox.backity.core.discovery.application;

public interface GameDiscoveryProgressTracker {

    void incrementGamesDiscovered(int howMuch);

    void initializeGamesDiscovered(long totalElements);
}
