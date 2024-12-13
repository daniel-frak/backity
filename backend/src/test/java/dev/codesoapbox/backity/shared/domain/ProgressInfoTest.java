package dev.codesoapbox.backity.shared.domain;

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