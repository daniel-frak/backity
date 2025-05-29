package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FilePathMustNotBeNullForStoredFileCopy;
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
                    FileCopyStatus.TRACKED,
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
                    FileCopyStatus.TRACKED,
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
                    FileCopyStatus.TRACKED,
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
        void constructorShouldThrowGivenStoredUnverifiedWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    naturalId,
                    FileCopyStatus.STORED_INTEGRITY_UNKNOWN,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("filePath is required");
        }

        @Test
        void constructorShouldThrowGivenStoredVerifiedWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    naturalId,
                    FileCopyStatus.STORED_INTEGRITY_VERIFIED,
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
                    FileCopyStatus.TRACKED,
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
        void toTrackedShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toTracked();

            FileCopy expectedResult = TestFileCopy.trackedBuilder()
                    .failedReason(null)
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toTrackedShouldTransitionFromStoredUnverifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            fileCopy.toTracked();

            FileCopy expectedResult = TestFileCopy.trackedBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toTrackedShouldTransitionFromStoredVerifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityVerified();

            fileCopy.toTracked();

            FileCopy expectedResult = TestFileCopy.trackedBuilder()
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
        void toEnqueuedShouldTransitionFromStoredUnverifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            fileCopy.toEnqueued();

            FileCopy expectedResult = TestFileCopy.enqueuedBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void toEnqueuedShouldTransitionFromStoredVerifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityVerified();

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
        void toInProgressShouldTransitionFromStoredUnverifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            fileCopy.toInProgress();

            FileCopy expectedResult = TestFileCopy.inProgressWithoutFilePathBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldTransitionFromStoredVerifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityVerified();

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
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
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
        void toStoredIntegrityUnknownShouldThrowGivenNullFilePath() {
            FileCopy fileCopy = TestFileCopy.inProgressWithoutFilePath();

            assertThatThrownBy(() -> fileCopy.toStoredIntegrityUnknown(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filePath");
        }

        @Test
        void toStoredIntegrityUnknownShouldTransitionFromFailedAndLoseFailedReason() {
            FileCopy fileCopy = TestFileCopy.failed();

            fileCopy.toStoredIntegrityUnknown("someFilePath");

            FileCopy expectedResult = TestFileCopy.storedIntegrityUnknown();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toStoredIntegrityUnknownShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.inProgressWithoutFilePath();
            FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(fileCopy);

            fileCopy.toStoredIntegrityUnknown("someFilePath");

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
        void toFailedShouldTransitionFromStoredUnverifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            fileCopy.toFailed("someFailedReason");

            FileCopy expectedResult = TestFileCopy.failedBuilder()
                    .filePath(fileCopy.getFilePath())
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toFailedShouldTransitionFromStoredVerifiedAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityVerified();

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
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
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
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
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
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            assertThatThrownBy(() -> fileCopy.setFilePath(null))
                    .isInstanceOf(FilePathMustNotBeNullForStoredFileCopy.class);
        }
    }

    @Nested
    class Validation {

        @Test
        void validateIsBackedUpShouldDoNothingGivenStatusIsStoredIntegrityUnknown() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            assertThatCode(fileCopy::validateIsBackedUp)
                    .doesNotThrowAnyException();
        }

        @Test
        void validateIsBackedUpShouldThrowGivenStatusIsNotStored() {
            FileCopy fileCopy = TestFileCopy.tracked();

            assertThatThrownBy(fileCopy::validateIsBackedUp)
                    .isInstanceOf(FileCopyNotBackedUpException.class)
                    .hasMessageContaining(fileCopy.getId().toString());
        }
    }

    @Nested
    class GeneralDomainEvents {

        @Test
        void shouldClearDomainEvents() {
            FileCopy fileCopy = TestFileCopy.tracked();
            fileCopy.toInProgress();

            fileCopy.clearDomainEvents();

            assertThat(fileCopy.getDomainEvents()).isEmpty();
        }

        @Test
        void getDomainEventsShouldReturnUnmodifiableList() {
            FileCopy fileCopy = TestFileCopy.tracked();

            List<DomainEvent> result = fileCopy.getDomainEvents();

            assertThatThrownBy(result::clear)
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}