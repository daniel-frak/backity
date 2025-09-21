package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyReplicationProgressChangedEventTest {

    @Nested
    class Creation {

        private static final FileCopyId TEST_FILE_COPY_ID = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        private static final FileCopyNaturalId TEST_FILE_COPY_NATURAL_ID = new FileCopyNaturalId(
                new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"),
                new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76")
        );
        private static final int TEST_PERCENTAGE = 25;
        private static final Duration TEST_TIME_LEFT = Duration.ofSeconds(99);

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullFileCopyId() {
            assertThatThrownBy(() -> new FileCopyReplicationProgressChangedEvent(
                    null,
                    TEST_FILE_COPY_NATURAL_ID,
                    TEST_PERCENTAGE,
                    TEST_TIME_LEFT
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileCopyId");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullFileCopyNaturalId() {
            assertThatThrownBy(() -> new FileCopyReplicationProgressChangedEvent(
                    TEST_FILE_COPY_ID,
                    null,
                    TEST_PERCENTAGE,
                    TEST_TIME_LEFT
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileCopyNaturalId");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullTimeLeft() {
            assertThatThrownBy(() -> new FileCopyReplicationProgressChangedEvent(
                    TEST_FILE_COPY_ID,
                    TEST_FILE_COPY_NATURAL_ID,
                    TEST_PERCENTAGE,
                    null
            ))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("timeLeft");
        }
    }
}