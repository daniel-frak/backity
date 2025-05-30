package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.InvalidFileCopyStatusTransitionException;
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
        void constructorShouldThrowGivenInProgressWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(GameFileId.newInstance(), BackupTargetId.newInstance());

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    naturalId,
                    FileCopyStatus.IN_PROGRESS,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("filePath is required");
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
            FileCopy fileCopy = TestFileCopy.failedWithoutFilePath();

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
            FileCopy fileCopy = TestFileCopy.failedWithoutFilePath();

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

        @SuppressWarnings("DataFlowIssue")
        @Test
        void toInProgressShouldThrowGivenNullFilePath() {
            FileCopy fileCopy = TestFileCopy.enqueued();

            assertThatThrownBy(() -> fileCopy.toInProgress(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("filePath");
        }

        @Test
        void toInProgressShouldThrowGivenNotTransitionNotFromEnqueued() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            assertThatThrownBy(() -> fileCopy.toInProgress("someFilePath"))
                    .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                    .hasMessageContaining(fileCopy.getId().toString())
                    .hasMessageContaining(FileCopyStatus.STORED_INTEGRITY_UNKNOWN.toString())
                    .hasMessageContaining(FileCopyStatus.IN_PROGRESS.toString());
        }

        @Test
        void toInProgressShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.enqueued();

            fileCopy.toInProgress("someFilePath");

            var expectedEvent = new FileBackupStartedEvent(
                    fileCopy.getId(),
                    fileCopy.getNaturalId(),
                    "someFilePath"
            );
            assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.IN_PROGRESS);
            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        @Test
        void toStoredIntegrityUnknownShouldThrowGivenTransitionNotFromInProgress() {
            FileCopy fileCopy = TestFileCopy.failedWithoutFilePath();

            assertThatThrownBy(fileCopy::toStoredIntegrityUnknown)
                    .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                    .hasMessageContaining(fileCopy.getId().toString())
                    .hasMessageContaining(FileCopyStatus.FAILED.toString())
                    .hasMessageContaining(FileCopyStatus.STORED_INTEGRITY_UNKNOWN.toString());
        }

        @Test
        void toStoredIntegrityUnknownShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.inProgress();
            FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(fileCopy);

            fileCopy.toStoredIntegrityUnknown();

            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        private FileBackupFinishedEvent fileBackupFinishedEvent(FileCopy fileCopy) {
            return new FileBackupFinishedEvent(fileCopy.getId(), fileCopy.getNaturalId());
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void toFailedShouldThrowGivenNullFailedReason() {
            FileCopy fileCopy = TestFileCopy.inProgress();

            assertThatThrownBy(() -> fileCopy.toFailed(null, "someFilePath"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("failedReason");
        }

        @Test
        void toFailedShouldTransitionFromStoredUnverifiedAndUpdateFilePath() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            fileCopy.toFailed("someFailedReason", "updatedFilePath");

            FileCopy expectedResult = TestFileCopy.failedWithFilePathBuilder()
                    .filePath("updatedFilePath")
                    .build();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toFailedShouldClearFilePathGivenNullWasPassed() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            fileCopy.toFailed("someFailedReason", null);

            FileCopy expectedResult = TestFileCopy.failedWithoutFilePath();
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toFailedShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
            String failedReason = "someFailedReason";
            FileBackupFailedEvent expectedEvent = fileBackupFailedEvent(fileCopy, failedReason);

            fileCopy.toFailed(failedReason, "someFilePath");

            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        private FileBackupFailedEvent fileBackupFailedEvent(FileCopy fileCopy, String failedReason) {
            return new FileBackupFailedEvent(fileCopy.getId(), fileCopy.getNaturalId(), failedReason);
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
            FileCopy fileCopy = TestFileCopy.enqueued();
            fileCopy.toInProgress("someFilePath");

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