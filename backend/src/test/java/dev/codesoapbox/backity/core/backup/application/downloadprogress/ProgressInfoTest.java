package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressInfoTest {

    @Test
    void noneShouldCreateInfoWithNoProgress() {
        var result = ProgressInfo.none();

        assertThat(result.percentage()).isZero();
        assertThat(result.timeLeft()).isNull();
    }
}