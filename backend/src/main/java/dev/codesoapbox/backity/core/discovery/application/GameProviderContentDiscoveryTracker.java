package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class GameProviderContentDiscoveryTracker {

    private final GameProviderId gameProviderId;
    private boolean isInProgress;
    private int gamesDiscovered = 0;
    private int gameFilesDiscovered = 0;
    private ProgressInfo progressInfo;
    private LocalDateTime startedAt;
    private LocalDateTime stoppedAt;

    // Status is FAILED unless marked as successful
    private GameContentDiscoveryOutcome discoveryOutcome = GameContentDiscoveryOutcome.FAILED;

    private LocalDateTime lastSuccessfulDiscoveryCompletedAt;

    public void incrementGamesDiscovered(int howMuch) {
        gamesDiscovered += howMuch;
    }

    public void incrementGameFilesDiscovered(int howMuch) {
        gameFilesDiscovered += howMuch;
    }

    public void updateProgressInfo(ProgressInfo progressInfo) {
        this.progressInfo = progressInfo;
    }

    public GameContentDiscoveryProgress getProgress() {
        if(progressInfo == null || !isInProgress) {
            return null;
        }
        return new GameContentDiscoveryProgress(
                gameProviderId, progressInfo.percentage(), progressInfo.timeLeft(),
                gamesDiscovered, gameFilesDiscovered
        );
    }

    public GameContentDiscoveryResult getResult() {
        return new GameContentDiscoveryResult(
                gameProviderId, startedAt, stoppedAt, discoveryOutcome,
                lastSuccessfulDiscoveryCompletedAt, gamesDiscovered, gameFilesDiscovered);
    }
}
