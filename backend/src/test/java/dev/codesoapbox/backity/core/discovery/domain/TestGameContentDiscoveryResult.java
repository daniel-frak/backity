package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(builderClassName = "Builder", builderMethodName = "", buildMethodName = "internalBuild", setterPrefix = "with")
public class TestGameContentDiscoveryResult {

    private GameProviderId gameProviderId;

    @lombok.Builder.Default
    private LocalDateTime startedAt = LocalDateTime.parse("2022-04-29T15:00:00");

    @lombok.Builder.Default
    private LocalDateTime stoppedAt = LocalDateTime.parse("2022-04-29T16:00:00");

    @lombok.Builder.Default
    private GameContentDiscoveryOutcome discoveryOutcome = GameContentDiscoveryOutcome.SUCCESS;

    @lombok.Builder.Default
    private LocalDateTime lastSuccessfulDiscoveryCompletedAt = LocalDateTime.parse("2022-04-20T10:00:00");

    @lombok.Builder.Default
    private int gamesDiscovered = 5;

    @lombok.Builder.Default
    private int sourceFilesDiscovered = 70;

    public static GameContentDiscoveryResult gog() {
        return gogBuilder()
                .withGameProviderId(new GameProviderId("GOG"))
                .build();
    }

    public static Builder gogBuilder() {
        return new Builder();
    }

    public static class Builder {

        public GameContentDiscoveryResult build() {
            TestGameContentDiscoveryResult temp = internalBuild();
            return new GameContentDiscoveryResult(temp.gameProviderId, temp.startedAt, temp.stoppedAt,
                    temp.discoveryOutcome, temp.lastSuccessfulDiscoveryCompletedAt, temp.gamesDiscovered,
                    temp.sourceFilesDiscovered);
        }
    }
}