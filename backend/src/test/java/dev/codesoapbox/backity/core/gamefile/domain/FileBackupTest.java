package dev.codesoapbox.backity.core.gamefile.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileBackupTest {

    @Nested
    class Creation {

        @Test
        void shouldCreate() {
            var result = new FileBackup(FileBackupStatus.DISCOVERED, null, null);

            assertThat(result).isNotNull();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullStatus() {
            assertThatThrownBy(() -> new FileBackup(
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("status is marked non-null but is null");
        }

        @Test
        void constructorShouldThrowGivenFailedWithoutReason() {
            assertThatThrownBy(() -> new FileBackup(
                    FileBackupStatus.FAILED,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("failedReason is required");
        }

        @Test
        void constructorShouldThrowGivenSuccessfulWithoutFilePath() {
            assertThatThrownBy(() -> new FileBackup(
                    FileBackupStatus.SUCCESS,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("filePath is required");
        }

        @Test
        void constructorShouldThrowGivenFailedReasonButNotFailed() {
            assertThatThrownBy(() -> new FileBackup(
                    FileBackupStatus.DISCOVERED,
                    "someFailedReason",
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("failedReason must be null for this status");
        }
    }

    @Nested
    class Transitions {
        @Test
        void toDiscoveredShouldReturnNewInstanceWithoutFailedReason() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileBackup result = fileBackup.toDiscovered();

            var expectedResult = new FileBackup(
                    FileBackupStatus.DISCOVERED,
                    null,
                    null
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toDiscoveredShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileBackup result = fileBackup.toDiscovered();

            var expectedResult = new FileBackup(
                    FileBackupStatus.DISCOVERED,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldReturnNewInstanceWithoutFailedReason() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileBackup result = fileBackup.toEnqueued();

            var expectedResult = new FileBackup(
                    FileBackupStatus.ENQUEUED,
                    null,
                    null
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileBackup result = fileBackup.toEnqueued();

            var expectedResult = new FileBackup(
                    FileBackupStatus.ENQUEUED,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldReturnNewInstanceWithoutFailedReason() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileBackup result = fileBackup.toInProgress();

            var expectedResult = new FileBackup(
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    null
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileBackup result = fileBackup.toInProgress();

            var expectedResult = new FileBackup(
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toSuccessfulShouldReturnNewInstanceWithoutFailedReason() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileBackup result = fileBackup.toSuccessful("someFilePath");

            var expectedResult = new FileBackup(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toFailedShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileBackup(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileBackup result = fileBackup.toFailed("someFailedReason");

            var expectedResult = new FileBackup(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }
    }
}