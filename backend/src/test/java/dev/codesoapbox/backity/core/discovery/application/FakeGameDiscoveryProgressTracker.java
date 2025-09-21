package dev.codesoapbox.backity.core.discovery.application;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class FakeGameDiscoveryProgressTracker implements GameDiscoveryProgressTracker {

    @Getter
    private final List<Integer> historicalDiscoveredGamesCount = new ArrayList<>();

    @Getter
    private long gamesDiscovered;

    @Getter
    private long totalGames;

    @Override
    public void incrementGamesDiscovered(int howMuch) {
        gamesDiscovered += howMuch;
        historicalDiscoveredGamesCount.add(howMuch);
    }

    @Override
    public void initializeGamesDiscovered(long totalElements) {
        totalGames = totalElements;
        gamesDiscovered = 0;
        historicalDiscoveredGamesCount.clear();
    }
}