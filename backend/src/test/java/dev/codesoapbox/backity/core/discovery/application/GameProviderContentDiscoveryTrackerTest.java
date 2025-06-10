package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GameProviderContentDiscoveryTrackerTest {

    private static final GameProviderId GAME_PROVIDER_ID = new GameProviderId("GOG");

    private GameProviderContentDiscoveryTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new GameProviderContentDiscoveryTracker(GAME_PROVIDER_ID);
    }

    @Test
    void shouldIncrementGamesDiscovered() {
        tracker.incrementGamesDiscovered(5);
        tracker.incrementGamesDiscovered(5);

        assertThat(tracker.getGamesDiscovered()).isEqualTo(10);
    }

    @Test
    void shouldIncrementGameFilesDiscovered() {
        tracker.incrementGameFilesDiscovered(5);
        tracker.incrementGameFilesDiscovered(5);

        assertThat(tracker.getGameFilesDiscovered()).isEqualTo(10);
    }

    @Test
    void shouldUpdateProgressInfo() {
        var progressInfo = new ProgressInfo(15, Duration.ofSeconds(99));

        tracker.updateProgressInfo(progressInfo);

        ProgressInfo result = tracker.getProgressInfo();
        assertThat(result).isEqualTo(progressInfo);
    }

    @Test
    void getProgressShouldReturnNullGivenNotInProgress() {
        var progressInfo = new ProgressInfo(15, Duration.ofSeconds(99));
        tracker.setInProgress(false);
        tracker.updateProgressInfo(progressInfo);

        GameContentDiscoveryProgress result = tracker.getProgress();

        assertThat(result).isNull();
    }

    @Test
    void getProgressShouldReturnNullGivenNoProgressInfo() {
        tracker.setInProgress(true);

        GameContentDiscoveryProgress result = tracker.getProgress();

        assertThat(result).isNull();
    }

    @Test
    void shouldGetProgress() {
        var progressInfo = new ProgressInfo(15, Duration.ofSeconds(99));
        tracker.setInProgress(true);
        tracker.incrementGamesDiscovered(5);
        tracker.incrementGameFilesDiscovered(10);
        tracker.updateProgressInfo(progressInfo);

        GameContentDiscoveryProgress result = tracker.getProgress();

        var expectedResult = new GameContentDiscoveryProgress(
                GAME_PROVIDER_ID,
                15,
                Duration.ofSeconds(99),
                5,
                10
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldGetDiscoveryResult() {
        tracker.incrementGamesDiscovered(5);
        tracker.incrementGameFilesDiscovered(10);
        tracker.setStartedAt(LocalDateTime.parse("1970-01-01T00:00:00"));
        tracker.setStoppedAt(LocalDateTime.parse("1970-01-01T01:00:00"));
        tracker.setLastSuccessfulDiscoveryCompletedAt(LocalDateTime.parse("1970-01-01T02:00:00"));

        GameContentDiscoveryResult result = tracker.getResult();

        var expectedResult = new GameContentDiscoveryResult(
                GAME_PROVIDER_ID,
                LocalDateTime.parse("1970-01-01T00:00:00"),
                LocalDateTime.parse("1970-01-01T01:00:00"),
                GameContentDiscoveryOutcome.FAILED,
                LocalDateTime.parse("1970-01-01T02:00:00"),
                5,
                10
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}