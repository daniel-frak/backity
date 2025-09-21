package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResultRepository;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class GameContentDiscoveryProgressTracker {

    private final Clock clock;
    private final DomainEventPublisher domainEventPublisher;
    private final GameContentDiscoveryResultRepository discoveryResultRepository;
    private final Map<GameProviderId, GameProviderContentDiscoveryTracker> trackersByGameProviderId =
            new ConcurrentHashMap<>();
    private final Function<GameProviderId, GameProviderContentDiscoveryTracker> providerTrackerFactory;

    public GameContentDiscoveryProgressTracker(
            Clock clock,
            DomainEventPublisher domainEventPublisher,
            GameContentDiscoveryResultRepository gameContentDiscoveryResultRepository,
            List<GameProviderFileDiscoveryService> gameProviderFileDiscoveryServices,
            Function<GameProviderId, GameProviderContentDiscoveryTracker> providerTrackerFactory) {
        this.clock = clock;
        this.domainEventPublisher = domainEventPublisher;
        this.discoveryResultRepository = gameContentDiscoveryResultRepository;
        this.providerTrackerFactory = providerTrackerFactory;
        initializeValues(gameProviderFileDiscoveryServices);
    }

    private void initializeValues(List<GameProviderFileDiscoveryService> gameProviderFileDiscoveryServices) {
        for (GameProviderFileDiscoveryService discoveryService : gameProviderFileDiscoveryServices) {
            resetStats(discoveryService.getGameProviderId());
        }
    }

    private void resetStats(GameProviderId gameProviderId) {
        trackersByGameProviderId.put(gameProviderId, providerTrackerFactory.apply(gameProviderId));
    }

    public boolean isInProgress(GameProviderFileDiscoveryService discoveryService) {
        GameProviderContentDiscoveryTracker tracker =
                trackersByGameProviderId.get(discoveryService.getGameProviderId());
        return tracker.isInProgress();
    }

    public List<GameContentDiscoveryOverview> getDiscoveryOverviews() {
        Map<GameProviderId, GameContentDiscoveryResult> discoveryResultsByGameProviderId =
                getDiscoveryResultsByGameProviderId();

        return trackersByGameProviderId.entrySet().stream()
                .map(entry -> new GameContentDiscoveryOverview(
                        entry.getKey(),
                        entry.getValue().isInProgress(),
                        entry.getValue().getProgress(),
                        discoveryResultsByGameProviderId.get(entry.getKey())
                ))
                .toList();
    }

    private Map<GameProviderId, GameContentDiscoveryResult> getDiscoveryResultsByGameProviderId() {
        return discoveryResultRepository.findAllByGameProviderIdIn(getGameProviderIds()).stream()
                .collect(toMap(GameContentDiscoveryResult::getGameProviderId, identity()));
    }

    private Set<GameProviderId> getGameProviderIds() {
        return trackersByGameProviderId.keySet();
    }

    public void incrementGamesDiscovered(GameProviderId gameProviderId, int howMuch) {
        trackersByGameProviderId.get(gameProviderId).incrementGamesDiscovered(howMuch);
    }

    public void incrementGameFilesDiscovered(GameProviderId gameProviderId, int howMuch) {
        trackersByGameProviderId.get(gameProviderId).incrementGameFilesDiscovered(howMuch);
    }

    public void initializeTracking(GameProviderId gameProviderId) {
        resetStats(gameProviderId);
        GameProviderContentDiscoveryTracker tracker = trackersByGameProviderId.get(gameProviderId);
        tracker.setInProgress(true);
        tracker.setStartedAt(LocalDateTime.now(clock));
        sendDiscoveryStartedEvent(gameProviderId);
    }

    private void sendDiscoveryStartedEvent(GameProviderId gameProviderId) {
        var event = new GameContentDiscoveryStartedEvent(gameProviderId);
        domainEventPublisher.publish(event);
    }

    public void finalizeTracking(GameProviderId gameProviderId) {
        GameProviderContentDiscoveryTracker tracker = trackersByGameProviderId.get(gameProviderId);
        tracker.setInProgress(false);
        tracker.setStoppedAt(LocalDateTime.now(clock));

        GameContentDiscoveryResult result = tracker.getResult();
        discoveryResultRepository.save(result);

        sendDiscoveryStoppedEvent(gameProviderId);
    }

    private void sendDiscoveryStoppedEvent(GameProviderId gameProviderId) {
        GameProviderContentDiscoveryTracker tracker = trackersByGameProviderId.get(gameProviderId);
        var event = new GameContentDiscoveryStoppedEvent(gameProviderId, tracker.getResult());
        domainEventPublisher.publish(event);
    }

    public void markSuccessful(GameProviderId gameProviderId) {
        GameProviderContentDiscoveryTracker tracker = trackersByGameProviderId.get(gameProviderId);
        tracker.setDiscoveryOutcome(GameContentDiscoveryOutcome.SUCCESS, clock);
    }

    public GameDiscoveryProgressTracker getGameDiscoveryTracker(GameProviderId gameProviderId) {
        return trackersByGameProviderId.get(gameProviderId);
    }
}
