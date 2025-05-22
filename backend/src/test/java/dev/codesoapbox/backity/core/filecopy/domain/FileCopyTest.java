package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FilePathMustNotBeNullForSuccessfulFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class FileCopyTest {

    @Nested
    class Creation {

        @Test
        void shouldCreate() {
            var result = new FileCopy(FileCopyId.newInstance(),
                    new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance()),
                    FileCopyStatus.DISCOVERED,
                    null, null, null, null);

            assertThat(result).isNotNull();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullId() {
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    null,
                    naturalId,
                    FileCopyStatus.DISCOVERED,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("id is marked non-null but is null");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullNaturalId() {
            FileCopyId id = FileCopyId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    null,
                    FileCopyStatus.DISCOVERED,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("naturalId is marked non-null but is null");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullStatus() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    naturalId,
                    null,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("status is marked non-null but is null");
        }

        @Test
        void constructorShouldThrowGivenFailedWithoutReason() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    naturalId,
                    FileCopyStatus.FAILED,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("failedReason is required");
        }

        @Test
        void constructorShouldThrowGivenSuccessfulWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    naturalId,
                    FileCopyStatus.SUCCESS,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("filePath is required");
        }

        @Test
        void constructorShouldThrowGivenFailedReasonButNotFailed() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    naturalId,
                    FileCopyStatus.DISCOVERED,
                    "someFailedReason",
                    null,
                    null,
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

            FileCopy expectedResult = TestFileCopy.discoveredBuilder()
                    .failedReason(null)
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toDiscoveredShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toDiscovered();

            FileCopy expectedResult = TestFileCopy.discoveredBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toEnqueued();

            FileCopy expectedResult = TestFileCopy.enqueuedBuilder()
                    .failedReason(null)
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toEnqueued();

            FileCopy expectedResult = TestFileCopy.enqueuedBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toInProgress();

            FileCopy expectedResult = TestFileCopy.inProgressWithoutFilePathBuilder()
                    .failedReason(null)
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toInProgress();

            FileCopy expectedResult = TestFileCopy.inProgressWithoutFilePathBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.successful();
            FileBackupStartedEvent expectedEvent = fileBackupStartedEvent(fileCopy);

            fileCopy.toInProgress();

            assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.IN_PROGRESS);
            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        private FileBackupStartedEvent fileBackupStartedEvent(FileCopy fileCopy) {

            return new FileBackupStartedEvent(
                    fileCopy.getId(),
                    fileCopy.getNaturalId(),
                    fileCopy.getFilePath()
            );
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void toSuccessfulShouldThrowGivenNullFilePath() {
            FileCopy fileCopy = TestFileCopy.inProgressWithoutFilePath();

            assertThatThrownBy(() -> fileCopy.toSuccessful(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filePath");
        }

        @Test
        void toSuccessfulShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toSuccessful("someFilePath");

            FileCopy expectedResult = TestFileCopy.successful();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toSuccessfulShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.inProgressWithoutFilePath();
            FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(fileCopy);

            fileCopy.toSuccessful("someFilePath");

            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        private FileBackupFinishedEvent fileBackupFinishedEvent(FileCopy fileCopy) {
            return new FileBackupFinishedEvent(fileCopy.getId(), fileCopy.getNaturalId());
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void toFailedShouldThrowGivenNullFailedReason() {
            FileCopy fileCopy = TestFileCopy.inProgressWithoutFilePath();

            assertThatThrownBy(() -> fileCopy.toFailed(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("failedReason");
        }

        @Test
        void toFailedShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toFailed("someFailedReason");

            FileCopy expectedResult = TestFileCopy.failedBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toFailedShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.successful();
            String failedReason = "someFailedReason";
            FileBackupFailedEvent expectedEvent = fileBackupFailedEvent(fileCopy, failedReason);

            fileCopy.toFailed(failedReason);

            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        private FileBackupFailedEvent fileBackupFailedEvent(FileCopy fileCopy, String failedReason) {
            return new FileBackupFailedEvent(fileCopy.getId(), fileCopy.getNaturalId(), failedReason);
        }
    }

    @Nested
    class FilePathModification {

        @Test
        void shouldSetFilePathGivenSuccess() {
            FileCopy fileCopy = TestFileCopy.successful();
            String expectedFilePath = "customFilePath";

            fileCopy.setFilePath(expectedFilePath);

            assertThat(fileCopy.getFilePath()).isEqualTo(expectedFilePath);
        }

        @Test
        void shouldSetFilePathToNullGivenNotSuccess() {
            FileCopy fileCopy = TestFileCopy.inProgressWithoutFilePathBuilder()
                    .filePath("someFilePath")
                    .build();

            fileCopy.setFilePath(null);

            assertThat(fileCopy.getFilePath()).isNull();
        }

        @Test
        void setFilePathShouldThrowGivenSettingToNullAndStatusIsSuccess() {
            FileCopy fileCopy = TestFileCopy.successful();

            assertThatThrownBy(() -> fileCopy.setFilePath(null))
                    .isInstanceOf(FilePathMustNotBeNullForSuccessfulFileCopy.class);
        }
    }

    @Nested
    class Validation {

        @Test
        void validateIsBackedUpShouldDoNothingGivenStatusIsSuccessful() {
            FileCopy fileCopy = TestFileCopy.successful();

            assertThatCode(fileCopy::validateIsBackedUp)
                    .doesNotThrowAnyException();
        }

        @Test
        void validateIsBackedUpShouldThrowGivenStatusIsNotSuccessful() {
            FileCopy fileCopy = TestFileCopy.discovered();

            assertThatThrownBy(fileCopy::validateIsBackedUp)
                    .isInstanceOf(FileCopyNotBackedUpException.class)
                    .hasMessageContaining(fileCopy.getId().toString());
        }
    }

    @Nested
    class GeneralDomainEvents {

        @Test
        void shouldClearDomainEvents() {
            FileCopy fileCopy = TestFileCopy.discovered();
            fileCopy.toInProgress();

            fileCopy.clearDomainEvents();

            assertThat(fileCopy.getDomainEvents()).isEmpty();
        }

        @Test
        void getDomainEventsShouldReturnUnmodifiableList() {
            FileCopy fileCopy = TestFileCopy.discovered();

            List<DomainEvent> result = fileCopy.getDomainEvents();

            assertThatThrownBy(result::clear)
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}