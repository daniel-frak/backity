package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.InvalidFileCopyStatusTransitionException;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyTest {

    private FileCopyFailureReason aFailedReason() {
        return new FileCopyFailureReason("someFailedReason");
    }

    @Nested
    class Constructor {

        @Test
        void shouldCreate() {
            var result = new FileCopy(FileCopyId.newInstance(),
                    new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance()),
                    FileCopyStatus.TRACKED,
                    null, null, null, null);

            assertThat(result).isNotNull();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void shouldThrowGivenNullId() {
            var naturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());

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
        void shouldThrowGivenNullNaturalId() {
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
        void shouldThrowGivenNullStatus() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());

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
        void shouldThrowGivenFailedWithoutReason() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());

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
        void shouldThrowGivenInProgressWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());

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
        void shouldThrowGivenStoredUnverifiedWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());

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
        void shouldThrowGivenStoredVerifiedWithoutFilePath() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());

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
        void shouldThrowGivenFailedReasonButNotFailed() {
            FileCopyId anId = FileCopyId.newInstance();
            var aNaturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());
            FileCopyFailureReason aFailedReason = aFailedReason();

            assertThatThrownBy(() -> new FileCopy(
                    anId,
                    aNaturalId,
                    FileCopyStatus.TRACKED,
                    aFailedReason,
                    null,
                    null,
                    null
            )).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("failedReason must be null for this status");
        }
    }

    @Nested
    class Transitions {

        private FileBackupFinishedEvent fileBackupFinishedEvent(FileCopy fileCopy, FileCopyStatus newStatus) {
            return new FileBackupFinishedEvent(fileCopy.getId(), fileCopy.getNaturalId(), newStatus);
        }

        @Nested
        class ToCanceled {

            @Test
            void shouldThrowGivenTransitionNotFromInProgress() {
                FileCopy fileCopy = TestFileCopy.enqueued();

                assertThatThrownBy(fileCopy::toCanceled)
                        .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                        .hasMessageContaining(fileCopy.getId().toString())
                        .hasMessageContaining(FileCopyStatus.ENQUEUED.toString())
                        .hasMessageContaining(FileCopyStatus.TRACKED.toString());
            }

            @Test
            void shouldTransitionFromInProgressAndLoseFilePath() {
                FileCopy fileCopy = TestFileCopy.inProgress();

                fileCopy.toCanceled();

                assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.TRACKED);
                assertThat(fileCopy.getFilePath()).isNull();
            }

            @Test
            void shouldAddEvent() {
                FileCopy fileCopy = TestFileCopy.inProgress();
                FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(fileCopy, FileCopyStatus.TRACKED);

                fileCopy.toCanceled();

                assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
            }
        }

        @Nested
        class ToTracked {

            @Test
            void shouldThrowGivenTransitionFromInProgress() {
                FileCopy fileCopy = TestFileCopy.inProgress();

                assertThatThrownBy(fileCopy::toTracked)
                        .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                        .hasMessageContaining(fileCopy.getId().toString())
                        .hasMessageContaining(FileCopyStatus.IN_PROGRESS.toString())
                        .hasMessageContaining(FileCopyStatus.TRACKED.toString());
            }

            @Test
            void shouldTransitionFromFailedAndLoseFailedReason() {
                FileCopy fileCopy = TestFileCopy.failedWithoutFilePath();

                fileCopy.toTracked();

                FileCopy expectedResult = TestFileCopy.trackedBuilder()
                        .failedReason(null)
                        .build();
                assertThat(fileCopy).usingRecursiveComparison()
                        .isEqualTo(expectedResult);
            }

            @Test
            void shouldTransitionFromStoredUnverifiedAndKeepFilePath() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

                fileCopy.toTracked();

                FileCopy expectedResult = TestFileCopy.trackedBuilder()
                        .filePath(fileCopy.getFilePath())
                        .build();
                assertThat(fileCopy).usingRecursiveComparison()
                        .isEqualTo(expectedResult);
            }

            @Test
            void shouldTransitionFromStoredVerifiedAndKeepFilePath() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityVerified();

                fileCopy.toTracked();

                FileCopy expectedResult = TestFileCopy.trackedBuilder()
                        .filePath(fileCopy.getFilePath())
                        .build();
                assertThat(fileCopy).usingRecursiveComparison()
                        .isEqualTo(expectedResult);
            }
        }

        @Nested
        class Enqueue {

            @Test
            void shouldAddEventGivenNotAlreadyEnqueued() {
                FileCopy fileCopy = TestFileCopy.tracked();
                var expectedEvent = new FileCopyEnqueuedEvent(
                        fileCopy.getId()
                );

                fileCopy.enqueue();

                assertThat(fileCopy.getDomainEvents())
                        .containsExactly(expectedEvent);
            }

            @Test
            void shouldDoNothingGivenAlreadyEnqueued() {
                FileCopy fileCopy = TestFileCopy.enqueued();
                FileCopyStatus initialStatus = fileCopy.getStatus();

                fileCopy.enqueue();

                assertThat(fileCopy.getStatus())
                        .isEqualTo(initialStatus);
                assertThat(fileCopy.getDomainEvents())
                        .isEmpty();
            }

            @Test
            void shouldTransitionFromFailedAndLoseFailedReason() {
                FileCopy fileCopy = TestFileCopy.failedWithoutFilePath();

                fileCopy.enqueue();

                fileCopy.clearDomainEvents();
                FileCopy expectedResult = TestFileCopy.enqueuedBuilder()
                        .failedReason(null)
                        .build();
                assertThat(fileCopy).usingRecursiveComparison()
                        .isEqualTo(expectedResult);
            }

            @Test
            void shouldTransitionFromStoredUnverifiedAndKeepFilePath() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

                fileCopy.enqueue();

                fileCopy.clearDomainEvents();
                FileCopy expectedResult = TestFileCopy.enqueuedBuilder()
                        .filePath(fileCopy.getFilePath())
                        .build();
                assertThat(fileCopy).usingRecursiveComparison()
                        .isEqualTo(expectedResult);
            }

            @Test
            void shouldTransitionFromStoredVerifiedAndKeepFilePath() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityVerified();

                fileCopy.enqueue();

                fileCopy.clearDomainEvents();
                FileCopy expectedResult = TestFileCopy.enqueuedBuilder()
                        .filePath(fileCopy.getFilePath())
                        .build();
                assertThat(fileCopy).usingRecursiveComparison()
                        .isEqualTo(expectedResult);
            }
        }

        @Nested
        class ToInProgress {

            @SuppressWarnings("DataFlowIssue")
            @Test
            void shouldThrowGivenNullFilePath() {
                FileCopy fileCopy = TestFileCopy.enqueued();

                assertThatThrownBy(() -> fileCopy.toInProgress(null))
                        .isInstanceOf(NullPointerException.class)
                        .hasMessageContaining("filePath");
            }

            @Test
            void shouldThrowGivenNotTransitioningFromEnqueued() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
                var aFilePath = new FilePath("someFilePath");

                assertThatThrownBy(() -> fileCopy.toInProgress(aFilePath))
                        .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                        .hasMessageContaining(fileCopy.getId().toString())
                        .hasMessageContaining(FileCopyStatus.STORED_INTEGRITY_UNKNOWN.toString())
                        .hasMessageContaining(FileCopyStatus.IN_PROGRESS.toString());
            }

            @Test
            void shouldChangeFileCopyStatus() {
                FileCopy fileCopy = TestFileCopy.enqueued();
                var aFilePath = new FilePath("someFilePath");

                fileCopy.toInProgress(aFilePath);

                assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.IN_PROGRESS);
            }

            @Test
            void shouldAddEvent() {
                FileCopy fileCopy = TestFileCopy.enqueued();
                var expectedEvent = new FileBackupStartedEvent(
                        fileCopy.getId(),
                        fileCopy.getNaturalId(),
                        new FilePath("someFilePath")
                );

                fileCopy.toInProgress(expectedEvent.filePath());

                assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
            }

            @Test
            void shouldDoNothingGivenAlreadyInProgress() {
                FileCopy fileCopy = TestFileCopy.inProgress();
                FileCopyStatus initialStatus = fileCopy.getStatus();
                FilePath initialFilePath = fileCopy.getFilePath();
                var changedFilePath = new FilePath(initialFilePath.toString() + "/changed");

                fileCopy.toInProgress(changedFilePath);

                assertThat(fileCopy.getStatus())
                        .isEqualTo(initialStatus);
                assertThat(fileCopy.getFilePath())
                        .isEqualTo(initialFilePath);
                assertThat(fileCopy.getDomainEvents())
                        .isEmpty();
            }
        }

        @Nested
        class ToStoredIntegrityUnknown {

            @Test
            void shouldThrowGivenTransitionNotFromInProgress() {
                FileCopy fileCopy = TestFileCopy.failedWithoutFilePath();

                assertThatThrownBy(fileCopy::toStoredIntegrityUnknown)
                        .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                        .hasMessageContaining(fileCopy.getId().toString())
                        .hasMessageContaining(FileCopyStatus.FAILED.toString())
                        .hasMessageContaining(FileCopyStatus.STORED_INTEGRITY_UNKNOWN.toString());
            }

            @Test
            void shouldAddEvent() {
                FileCopy fileCopy = TestFileCopy.inProgress();
                FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(
                        fileCopy, FileCopyStatus.STORED_INTEGRITY_UNKNOWN);

                fileCopy.toStoredIntegrityUnknown();

                assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
            }
        }

        @Nested
        class ToFailed {

            @SuppressWarnings("DataFlowIssue")
            @Test
            void shouldThrowGivenNullFailedReason() {
                FileCopy fileCopy = TestFileCopy.inProgress();
                FilePath filePath = aFilePath();

                assertThatThrownBy(() -> fileCopy.toFailed(null, filePath))
                        .isInstanceOf(NullPointerException.class)
                        .hasMessageContaining("failedReason");
            }

            private FilePath aFilePath() {
                return new FilePath("someFilePath");
            }

            @Test
            void shouldTransitionFromStoredUnverifiedAndUpdateFilePath() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
                var expectedFilePath = new FilePath("updatedFilePath");
                FileCopyFailureReason aFailedReason = aFailedReason();

                fileCopy.toFailed(aFailedReason, expectedFilePath);

                FileCopy expectedResult = TestFileCopy.failedWithFilePathBuilder()
                        .filePath(expectedFilePath)
                        .build();
                assertThat(fileCopy).usingRecursiveComparison()
                        .ignoringFields("domainEvents")
                        .isEqualTo(expectedResult);
            }

            @Test
            void shouldClearFilePathGivenNullWasPassed() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
                FileCopyFailureReason aFailedReason = aFailedReason();

                fileCopy.toFailed(aFailedReason, null);

                FileCopy expectedResult = TestFileCopy.failedWithoutFilePath();
                assertThat(fileCopy).usingRecursiveComparison()
                        .ignoringFields("domainEvents")
                        .isEqualTo(expectedResult);
            }

            @Test
            void shouldAddEvent() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
                var failedReason = aFailedReason();
                FileBackupFailedEvent expectedEvent = fileBackupFailedEvent(fileCopy, failedReason);

                fileCopy.toFailed(failedReason, aFilePath());

                assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
            }

            private FileBackupFailedEvent fileBackupFailedEvent(FileCopy fileCopy, FileCopyFailureReason failedReason) {
                return new FileBackupFailedEvent(fileCopy.getId(), fileCopy.getNaturalId(), failedReason);
            }
        }
    }

    @Nested
    class IsStored {

        @Test
        void shouldReturnTrueGivenStatusIsStoredIntegrityUnknown() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

            assertThat(fileCopy.isStored()).isTrue();
        }

        @Test
        void shouldReturnTrueGivenStatusIsStoredIntegrityVerified() {
            FileCopy fileCopy = TestFileCopy.storedIntegrityVerified();

            assertThat(fileCopy.isStored()).isTrue();
        }

        @Test
        void shouldReturnFalseGivenStatusIsNotStored() {
            FileCopy fileCopy = TestFileCopy.tracked();

            assertThat(fileCopy.isStored()).isFalse();
        }
    }

    @Nested
    class GeneralDomainEvents {

        @Nested
        class ClearDomainEvents {

            @Test
            void shouldClearDomainEvents() {
                FileCopy fileCopy = TestFileCopy.enqueued();
                var aFilePath = new FilePath("someFilePath");
                fileCopy.toInProgress(aFilePath);

                fileCopy.clearDomainEvents();

                assertThat(fileCopy.getDomainEvents()).isEmpty();
            }
        }

        @Nested
        class GetDomainEvents {

            @Test
            void shouldReturnUnmodifiableList() {
                FileCopy fileCopy = TestFileCopy.tracked();

                List<DomainEvent> result = fileCopy.getDomainEvents();

                assertThatThrownBy(result::clear)
                        .isInstanceOf(UnsupportedOperationException.class);
            }
        }
    }
}