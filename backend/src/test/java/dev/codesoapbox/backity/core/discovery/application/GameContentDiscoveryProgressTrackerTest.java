package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResultRepository;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.time.FakeClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameContentDiscoveryProgressTrackerTest {

    private GameContentDiscoveryProgressTracker tracker;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private GameContentDiscoveryResultRepository discoveryResultRepository;

    @Mock
    private GameProviderContentDiscoveryTracker providerTracker;

    private FakeGameProviderFileDiscoveryService gameProviderFileDiscoveryService;

    @Mock
    private GameProviderFileDiscoveryService anotherGameProviderFileDiscoveryService;

    private FakeClock clock;

    private int providerTrackerResetCounter = 0;

    @BeforeEach
    void setUp() {
        clock = FakeClock.atEpochUtc();
        gameProviderFileDiscoveryService = new FakeGameProviderFileDiscoveryService();
        when(anotherGameProviderFileDiscoveryService.getGameProviderId())
                .thenReturn(new GameProviderId("anotherGameProviderId"));
        tracker = new GameContentDiscoveryProgressTracker(
                clock, domainEventPublisher, discoveryResultRepository,
                List.of(gameProviderFileDiscoveryService, anotherGameProviderFileDiscoveryService),
                resetProviderTrackerWhenNewRequested());
    }

    private Function<GameProviderId, GameProviderContentDiscoveryTracker> resetProviderTrackerWhenNewRequested() {
        return (GameProviderId gameProviderId) -> {
            if(!gameProviderId.equals(gameProviderFileDiscoveryService.getGameProviderId())) {
                // Return different tracker for anotherGameProviderFileDiscoveryService
                return mock(GameProviderContentDiscoveryTracker.class);
            }
            providerTrackerResetCounter++;
            reset(providerTracker);
            return providerTracker;
        };
    }

    @Nested
    class IsInProgress {

        @Test
        void isInProgressShouldReturnFalseGivenNotInProgress() {
            when(providerTracker.isInProgress())
                    .thenReturn(false);

            assertThat(tracker.isInProgress(gameProviderFileDiscoveryService)).isFalse();
        }

        @Test
        void isInProgressShouldReturnTrueGivenInProgress() {
            when(providerTracker.isInProgress())
                    .thenReturn(true);

            assertThat(tracker.isInProgress(gameProviderFileDiscoveryService)).isTrue();
        }
    }

    @Nested
    class InitializingTracking {

        @Test
        void initializeTrackingShouldSetInProgressToTrue() {
            tracker.initializeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            verify(providerTracker).setInProgress(true);
        }

        @Test
        void initializeTrackingShouldResetStats() {
            providerTrackerResetCounter = 0;

            tracker.initializeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            assertThat(providerTrackerResetCounter).isEqualTo(1);
        }

        @Test
        void initializeTrackingShouldSetStartedAt() {
            providerTrackerResetCounter = 0;

            tracker.initializeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            verify(providerTracker).setStartedAt(LocalDateTime.parse("1970-01-01T00:00:00"));
        }

        @Test
        void initializeTrackingShouldSendDiscoveryStartedEvent() {
            tracker.initializeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            var discoveryStartedEvent = new GameContentDiscoveryStartedEvent(
                    gameProviderFileDiscoveryService.getGameProviderId());
            verify(domainEventPublisher).publish(discoveryStartedEvent);
        }
    }

    @Nested
    class IncrementingDiscoveries {

        @Test
        void incrementGamesDiscoveredShouldIncrementGamesDiscovered() {
            tracker.incrementGamesDiscovered(gameProviderFileDiscoveryService.getGameProviderId(), 5);

            verify(providerTracker).incrementGamesDiscovered(5);
        }

        @Test
        void incrementGameFilesDiscoveredShouldIncrementGameFilesDiscovered() {
            tracker.incrementGameFilesDiscovered(gameProviderFileDiscoveryService.getGameProviderId(), 5);

            verify(providerTracker).incrementGameFilesDiscovered(5);
        }
    }

    @Nested
    class FinalizingTracking {

        @Test
        void finalizeTrackingShouldSetInProgressToFalse() {
            tracker.initializeTracking(
                    gameProviderFileDiscoveryService.getGameProviderId()); // To set in progressTracker to true
            reset(providerTracker); // To make sure any assertions on behavior don't include setup behavior
            mockProviderTrackerReturnsDiscoveryResult();

            tracker.finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            verify(providerTracker).setInProgress(false);
        }

        private GameContentDiscoveryResult mockProviderTrackerReturnsDiscoveryResult() {
            GameContentDiscoveryResult result = mock(GameContentDiscoveryResult.class);
            lenient().when(providerTracker.getResult())
                    .thenReturn(result);
            return result;
        }

        @Test
        void finalizeTrackingShouldSetStoppedAt() {
            mockProviderTrackerReturnsDiscoveryResult();

            tracker.finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            verify(providerTracker).setStoppedAt(LocalDateTime.parse("1970-01-01T00:00:00"));
        }

        @Test
        void finalizeTrackingShouldSaveDiscoveryResult() {
            GameContentDiscoveryResult discoveryResult = mockProviderTrackerReturnsDiscoveryResult();

            tracker.finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            verify(discoveryResultRepository).save(discoveryResult);
        }

        @Test
        void finalizeTrackingShouldSendDiscoveryStoppedEvent() {
            GameContentDiscoveryResult discoveryResult = mockProviderTrackerReturnsDiscoveryResult();

            tracker.finalizeTracking(gameProviderFileDiscoveryService.getGameProviderId());

            var discoveryStoppedEvent = new GameContentDiscoveryStoppedEvent(
                    gameProviderFileDiscoveryService.getGameProviderId(), discoveryResult);
            verify(domainEventPublisher).publish(discoveryStoppedEvent);
        }
    }

    @Nested
    class MarkingSuccessful {

        @Test
        void markSuccessfulShouldSetDiscoveryOutcomeAsSuccess() {
            tracker.markSuccessful(gameProviderFileDiscoveryService.getGameProviderId());

            verify(providerTracker).setDiscoveryOutcome(GameContentDiscoveryOutcome.SUCCESS, clock);
        }
    }

    @Nested
    class GetGameDiscoveryTracker {

        @Test
        void shouldReturnProgressTrackerUniqueToGameProvider() {
            GameDiscoveryProgressTracker gameProvider1Tracker =
                    tracker.getGameDiscoveryTracker(gameProviderFileDiscoveryService.getGameProviderId());
            GameDiscoveryProgressTracker gameProvider2Tracker =
                    tracker.getGameDiscoveryTracker(anotherGameProviderFileDiscoveryService.getGameProviderId());

            assertThat(gameProvider1Tracker).isNotEqualTo(gameProvider2Tracker);
        }
    }

    @Nested
    class DiscoveryOverviews {

        @Test
        void shouldGetDiscoveryOverviews() {
            GameProviderId gameProviderId = gameProviderFileDiscoveryService.getGameProviderId();
            mockProviderTrackerIsInProgress();
            GameContentDiscoveryProgress discoveryProgress = mockProviderTrackerReturnsProgress();
            GameContentDiscoveryResult contentDiscoveryResult = mockDiscoveryResultExistsInRepository(gameProviderId);

            List<GameContentDiscoveryOverview> result = tracker.getDiscoveryOverviews();

            GameContentDiscoveryOverview expectedResult = new GameContentDiscoveryOverview(
                            gameProviderId, true, discoveryProgress, contentDiscoveryResult);
            assertThat(result).contains(expectedResult);
        }

        private GameContentDiscoveryResult mockDiscoveryResultExistsInRepository(GameProviderId gameProviderId) {
            GameContentDiscoveryResult contentDiscoveryResult = mock(GameContentDiscoveryResult.class);
            lenient().when(contentDiscoveryResult.getGameProviderId())
                    .thenReturn(gameProviderId);
            when(discoveryResultRepository.findAllByGameProviderIdIn(Set.of(gameProviderId,
                    anotherGameProviderFileDiscoveryService.getGameProviderId())))
                    .thenReturn(List.of(contentDiscoveryResult));
            return contentDiscoveryResult;
        }

        private void mockProviderTrackerIsInProgress() {
            when(providerTracker.isInProgress())
                    .thenReturn(true);
        }

        private GameContentDiscoveryProgress mockProviderTrackerReturnsProgress() {
            GameContentDiscoveryProgress discoveryProgress = mock(GameContentDiscoveryProgress.class);
            when(providerTracker.getProgress())
                    .thenReturn(discoveryProgress);
            return discoveryProgress;
        }
    }
}