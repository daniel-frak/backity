package dev.codesoapbox.backity.core.gamefile.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyTest {

    @Nested
    class Creation {

        @Test
        void shouldCreate() {
            var result = new FileCopy(FileBackupStatus.DISCOVERED, null, null);

            assertThat(result).isNotNull();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullStatus() {
            assertThatThrownBy(() -> new FileCopy(
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("status is marked non-null but is null");
        }

        @Test
        void constructorShouldThrowGivenFailedWithoutReason() {
            assertThatThrownBy(() -> new FileCopy(
                    FileBackupStatus.FAILED,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("failedReason is required");
        }

        @Test
        void constructorShouldThrowGivenSuccessfulWithoutFilePath() {
            assertThatThrownBy(() -> new FileCopy(
                    FileBackupStatus.SUCCESS,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("filePath is required");
        }

        @Test
        void constructorShouldThrowGivenFailedReasonButNotFailed() {
            assertThatThrownBy(() -> new FileCopy(
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
            var fileBackup = new FileCopy(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileCopy result = fileBackup.toDiscovered();

            var expectedResult = new FileCopy(
                    FileBackupStatus.DISCOVERED,
                    null,
                    null
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toDiscoveredShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileCopy(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileCopy result = fileBackup.toDiscovered();

            var expectedResult = new FileCopy(
                    FileBackupStatus.DISCOVERED,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldReturnNewInstanceWithoutFailedReason() {
            var fileBackup = new FileCopy(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileCopy result = fileBackup.toEnqueued();

            var expectedResult = new FileCopy(
                    FileBackupStatus.ENQUEUED,
                    null,
                    null
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileCopy(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileCopy result = fileBackup.toEnqueued();

            var expectedResult = new FileCopy(
                    FileBackupStatus.ENQUEUED,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldReturnNewInstanceWithoutFailedReason() {
            var fileBackup = new FileCopy(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileCopy result = fileBackup.toInProgress();

            var expectedResult = new FileCopy(
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    null
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileCopy(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileCopy result = fileBackup.toInProgress();

            var expectedResult = new FileCopy(
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toSuccessfulShouldReturnNewInstanceWithoutFailedReason() {
            var fileBackup = new FileCopy(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    null
            );

            FileCopy result = fileBackup.toSuccessful("someFilePath");

            var expectedResult = new FileCopy(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toFailedShouldReturnNewInstanceWithFilePath() {
            var fileBackup = new FileCopy(
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );

            FileCopy result = fileBackup.toFailed("someFailedReason");

            var expectedResult = new FileCopy(
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    "someFilePath"
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }
    }
}