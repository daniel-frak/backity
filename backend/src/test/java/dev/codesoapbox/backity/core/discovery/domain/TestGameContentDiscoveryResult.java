package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder(builderClassName = "Builder", builderMethodName = "gogBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public class TestGameContentDiscoveryResult {

    @lombok.Builder.Default
    private GameProviderId gameProviderId = new GameProviderId("GOG");

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
    private int gameFilesDiscovered = 70;

    public static GameContentDiscoveryResult gog() {
        return gogBuilder().build();
    }

    public static Builder gogBuilder() {
        return new Builder();
    }

    public static class Builder {

        public GameContentDiscoveryResult build() {
            TestGameContentDiscoveryResult temp = internalBuilder();
            return new GameContentDiscoveryResult(temp.gameProviderId, temp.startedAt, temp.stoppedAt,
                    temp.discoveryOutcome, temp.lastSuccessfulDiscoveryCompletedAt, temp.gamesDiscovered,
                    temp.gameFilesDiscovered);
        }
    }
}