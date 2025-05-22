package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.gamefile.domain.exceptions.FilePathMustNotBeNullForSuccessfulFileCopy;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyTest {

    @Nested
    class Creation {

        @Test
        void shouldCreate() {
            var result = new FileCopy(
                    FileCopyId.newInstance(), FileBackupStatus.DISCOVERED, null, null);

            assertThat(result).isNotNull();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullId() {
            assertThatThrownBy(() -> new FileCopy(
                    null,
                    FileBackupStatus.DISCOVERED,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("id is marked non-null but is null");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullStatus() {
            FileCopyId id = FileCopyId.newInstance();
            assertThatThrownBy(() -> new FileCopy(
                    id,
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("status is marked non-null but is null");
        }

        @Test
        void constructorShouldThrowGivenFailedWithoutReason() {
            FileCopyId id = FileCopyId.newInstance();
            assertThatThrownBy(() -> new FileCopy(
                    id,
                    FileBackupStatus.FAILED,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("failedReason is required");
        }

        @Test
        void constructorShouldThrowGivenSuccessfulWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            assertThatThrownBy(() -> new FileCopy(
                    id,
                    FileBackupStatus.SUCCESS,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("filePath is required");
        }

        @Test
        void constructorShouldThrowGivenFailedReasonButNotFailed() {
            FileCopyId id = FileCopyId.newInstance();
            assertThatThrownBy(() -> new FileCopy(
                    id,
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
        void toDiscoveredShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toDiscovered();

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.DISCOVERED,
                    null,
                    null
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toDiscoveredShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toDiscovered();

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.DISCOVERED,
                    null,
                    "someFilePath"
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toEnqueued();

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.ENQUEUED,
                    null,
                    null
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toEnqueued();

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.ENQUEUED,
                    null,
                    "someFilePath"
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toInProgress();

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    null
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toInProgress();

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    "someFilePath"
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void toSuccessfulShouldThrowGivenNullFilePath() {
            FileCopy fileCopy = TestFileCopy.inProgress();

            assertThatThrownBy(() -> fileCopy.toSuccessful(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filePath");
        }

        @Test
        void toSuccessfulShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toSuccessful("someFilePath");

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath"
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void toFailedShouldThrowGivenNullFailedReason() {
            FileCopy fileCopy = TestFileCopy.inProgress();

            assertThatThrownBy(() -> fileCopy.toFailed(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("failedReason");
        }

        @Test
        void toFailedShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toFailed("someFailedReason");

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    "someFilePath"
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }
    }

    @Nested
    class FilePathModification {

        @Test
        void shouldSetFilePath() {
            FileCopy fileCopy = TestFileCopy.inProgressBuilder()
                    .filePath(null)
                    .build();
            String expectedFilePath = "customFilePath";

            fileCopy.setFilePath(expectedFilePath);

            assertThat(fileCopy.getFilePath()).isEqualTo(expectedFilePath);
        }

        @Test
        void setFilePathShouldThrowGivenSettingToNullAndStatusIsSuccess() {
            FileCopy fileCopy = TestFileCopy.successful();

            assertThatThrownBy(() -> fileCopy.setFilePath(null))
                    .isInstanceOf(FilePathMustNotBeNullForSuccessfulFileCopy.class);
        }
    }
}