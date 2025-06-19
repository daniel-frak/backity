package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import lombok.Builder;

import java.time.Duration;

@Builder(builderClassName = "Builder", builderMethodName = "twentyFivePercentBuilder",
        buildMethodName = "internalBuilder", setterPrefix = "with")
public class TestFileCopyReplicationProgress {

    @lombok.Builder.Default
    private FileCopyId fileCopyId = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");

    @lombok.Builder.Default
    private int percentage = 25;

    @lombok.Builder.Default
    private Duration timeLeft = Duration.ofSeconds(10);

    public static FileCopyReplicationProgress twentyFivePercent() {
        return twentyFivePercentBuilder().build();
    }

    public static TestFileCopyReplicationProgress.Builder twentyFivePercentBuilder() {
        return new TestFileCopyReplicationProgress.Builder();
    }

    public static class Builder {

        public FileCopyReplicationProgress build() {
            TestFileCopyReplicationProgress temp = internalBuilder();
            return new FileCopyReplicationProgress(temp.fileCopyId, temp.percentage, temp.timeLeft);
        }
    }
}