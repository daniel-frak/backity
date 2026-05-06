package dev.codesoapbox.backity.shared.application.progress;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProgressInfoTest {

    @Nested
    class None {

        @Test
        void shouldCreateInfoWithNoProgress() {
            var result = ProgressInfo.none();

            assertThat(result.percentage()).isZero();
            assertThat(result.timeLeft()).isNull();
        }
    }
}