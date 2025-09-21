package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.shared.application.progress.ProgressTracker;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.time.FakeClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class GameProviderContentDiscoveryTrackerTest {

    private static final GameProviderId GAME_PROVIDER_ID = new GameProviderId("GOG");

    private GameProviderContentDiscoveryTracker tracker;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private FakeClock clock;

    @BeforeEach
    void setUp() {
        clock = FakeClock.atEpochUtc();
        tracker = new GameProviderContentDiscoveryTracker(GAME_PROVIDER_ID, domainEventPublisher,
                new ProgressTracker(clock));
    }

    @Nested
    class Events {

        @Test
        void shouldPublishEventWithCorrectProgress() {
            tracker.initializeGamesDiscovered(15);
            clock.moveForward(Duration.ofSeconds(1));

            tracker.incrementGameFilesDiscovered(99);
            tracker.incrementGamesDiscovered(5);
            tracker.incrementGamesDiscovered(1);

            var expectedEvent1 = new GameContentDiscoveryProgressChangedEvent(
                    GAME_PROVIDER_ID,
                    33,
                    Duration.ofSeconds(2),
                    5,
                    99
            );
            var expectedEvent2 = new GameContentDiscoveryProgressChangedEvent(
                    GAME_PROVIDER_ID,
                    40,
                    Duration.ofMillis(1500),
                    6,
                    99
            );
            InOrder inOrder = inOrder(domainEventPublisher);
            inOrder.verify(domainEventPublisher).publish(expectedEvent1);
            inOrder.verify(domainEventPublisher).publish(expectedEvent2);
        }
    }

    @Nested
    class Progress {

        @Test
        void shouldReturnProgress() {
            tracker.initializeGamesDiscovered(15);
            clock.moveForward(Duration.ofSeconds(1));
            tracker.incrementGameFilesDiscovered(99);
            tracker.incrementGamesDiscovered(5);

            GameContentDiscoveryProgress result = tracker.getProgress();

            var expectedResult = new GameContentDiscoveryProgress(
                    GAME_PROVIDER_ID,
                    33,
                    Duration.ofSeconds(2),
                    5,
                    99
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }
    }

    @Nested
    class Result {

        @Test
        void shouldReturnResult() {
            LocalDateTime startedAt = LocalDateTime.now(clock);
            clock.moveForward(Duration.ofMinutes(1));
            LocalDateTime stoppedAt = LocalDateTime.now(clock);
            tracker.initializeGamesDiscovered(5);
            tracker.incrementGamesDiscovered(5);
            tracker.incrementGameFilesDiscovered(15);
            tracker.setDiscoveryOutcome(GameContentDiscoveryOutcome.SUCCESS, clock);
            tracker.setStartedAt(startedAt);
            tracker.setStoppedAt(stoppedAt);

            GameContentDiscoveryResult result = tracker.getResult();

            var expectedResult = new GameContentDiscoveryResult(
                    GAME_PROVIDER_ID,
                    startedAt,
                    stoppedAt,
                    GameContentDiscoveryOutcome.SUCCESS,
                    stoppedAt,
                    5,
                    15
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }
    }
}