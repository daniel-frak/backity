package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import lombok.Builder;

import java.time.Duration;


@Builder(builderClassName = "Builder", builderMethodName = "gogBuilder", buildMethodName = "internalBuilder",
        setterPrefix = "with")
public class TestGameContentDiscoveryProgress {

    @lombok.Builder.Default
    private GameProviderId gameProviderId = new GameProviderId("GOG");

    @lombok.Builder.Default
    private int percentage = 25;

    @lombok.Builder.Default
    private Duration timeLeft = Duration.ofSeconds(10);

    public static GameContentDiscoveryProgress twentyFivePercentGog() {
        return gogBuilder().build();
    }

    public static TestGameContentDiscoveryProgress.Builder gogBuilder() {
        return new TestGameContentDiscoveryProgress.Builder();
    }

    public static class Builder {

        public GameContentDiscoveryProgress build() {
            TestGameContentDiscoveryProgress temp = internalBuilder();
            return new GameContentDiscoveryProgress(temp.gameProviderId, temp.percentage, temp.timeLeft);
        }
    }
}