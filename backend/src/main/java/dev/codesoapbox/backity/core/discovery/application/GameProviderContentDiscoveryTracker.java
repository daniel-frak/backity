package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.shared.application.progress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.shared.application.progress.ProgressTracker;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Getter
@RequiredArgsConstructor
public class GameProviderContentDiscoveryTracker implements GameDiscoveryProgressTracker {

    private final GameProviderId gameProviderId;
    private final DomainEventPublisher domainEventPublisher;
    private final ProgressTracker gamesDiscoveredProgressTracker;
    private int gameFilesDiscovered = 0;

    @Setter
    private boolean isInProgress;

    @Setter
    private LocalDateTime startedAt;

    @Setter
    private LocalDateTime stoppedAt;

    // Status is FAILED unless marked as successful
    private GameContentDiscoveryOutcome discoveryOutcome = GameContentDiscoveryOutcome.FAILED;

    @Setter
    private LocalDateTime lastSuccessfulDiscoveryCompletedAt;

    public void setDiscoveryOutcome(GameContentDiscoveryOutcome discoveryOutcome, Clock clock) {
        this.discoveryOutcome = discoveryOutcome;
        this.lastSuccessfulDiscoveryCompletedAt = LocalDateTime.now(clock);
    }

    @Override
    public void incrementGamesDiscovered(int howMuch) {
        gamesDiscoveredProgressTracker.incrementBy(howMuch);
        publishGameContentDiscoveryProgressChangedEvent(gamesDiscoveredProgressTracker.getProgressInfo());
        log.debug("Game Provider content discovery progress: {}", gamesDiscoveredProgressTracker.getProgressInfo());
    }

    public void incrementGameFilesDiscovered(int howMuch) {
        gameFilesDiscovered += howMuch;
    }

    @Override
    public void initializeGamesDiscovered(long totalElements) {
        gamesDiscoveredProgressTracker.reset(totalElements);
    }

    private void publishGameContentDiscoveryProgressChangedEvent(ProgressInfo progressInfo) {
        GameContentDiscoveryProgressChangedEvent event = createEvent(progressInfo);
        domainEventPublisher.publish(event);
    }

    private GameContentDiscoveryProgressChangedEvent createEvent(ProgressInfo progressInfo) {
        int percentage = progressInfo.percentage();
        Duration timeLeft = progressInfo.timeLeft();

        return new GameContentDiscoveryProgressChangedEvent(gameProviderId, percentage, timeLeft,
                gamesDiscoveredProgressTracker.getProcessedElementsCount(), gameFilesDiscovered);
    }

    public GameContentDiscoveryProgress getProgress() {
        ProgressInfo progressInfo = gamesDiscoveredProgressTracker.getProgressInfo();
        Long gamesDiscovered = gamesDiscoveredProgressTracker.getProcessedElementsCount();
        return new GameContentDiscoveryProgress(
                gameProviderId, progressInfo.percentage(), progressInfo.timeLeft(),
                gamesDiscovered, gameFilesDiscovered
        );
    }

    public GameContentDiscoveryResult getResult() {
        return new GameContentDiscoveryResult(
                gameProviderId, startedAt, stoppedAt, discoveryOutcome,
                lastSuccessfulDiscoveryCompletedAt,
                gamesDiscoveredProgressTracker.getProcessedElementsCount(),
                gameFilesDiscovered);
    }
}
