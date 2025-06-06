package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.InvalidReplicationProgressPercentageException;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyReplicationProgressTest {

    @Nested
    class Creation {

        @ParameterizedTest
        @ValueSource(ints = {-1, 101})
        void shouldThrowGivenInvalidPercentage(int invalidPercentage) {
            var fileCopyId = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
            Duration duration = Duration.ofSeconds(1);

            assertThatThrownBy(() -> new FileCopyReplicationProgress(fileCopyId, invalidPercentage, duration))
                    .isInstanceOf(InvalidReplicationProgressPercentageException.class)
                    .hasMessageContaining(String.valueOf(invalidPercentage));
        }
    }
}