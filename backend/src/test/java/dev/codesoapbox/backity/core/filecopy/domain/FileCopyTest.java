package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyEnqueuedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.InvalidFileCopyStatusTransitionException;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyTest {

    @Nested
    class Creation {

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
        void constructorShouldThrowGivenNullId() {
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
        void constructorShouldThrowGivenFailedWithoutReason() {
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
        void constructorShouldThrowGivenInProgressWithoutFilePath() {
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
        void constructorShouldThrowGivenStoredUnverifiedWithoutFilePath() {
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
        void constructorShouldThrowGivenStoredVerifiedWithoutFilePath() {
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
        void constructorShouldThrowGivenFailedReasonButNotFailed() {
            FileCopyId id = FileCopyId.newInstance();
            var naturalId = new FileCopyNaturalId(SourceFileId.newInstance(), BackupTargetId.newInstance());

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

        private FileBackupFinishedEvent fileBackupFinishedEvent(FileCopy fileCopy, FileCopyStatus newStatus) {
            return new FileBackupFinishedEvent(fileCopy.getId(), fileCopy.getNaturalId(), newStatus);
        }

        @Nested
        class ToCanceled {

            @Test
            void toCanceledShouldThrowGivenTransitionNotFromInProgress() {
                FileCopy fileCopy = TestFileCopy.enqueued();

                assertThatThrownBy(fileCopy::toCanceled)
                        .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                        .hasMessageContaining(fileCopy.getId().toString())
                        .hasMessageContaining(FileCopyStatus.ENQUEUED.toString())
                        .hasMessageContaining(FileCopyStatus.TRACKED.toString());
            }

            @Test
            void toCanceledShouldTransitionFromInProgressAndLoseFilePath() {
                FileCopy fileCopy = TestFileCopy.inProgress();

                fileCopy.toCanceled();

                assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.TRACKED);
                assertThat(fileCopy.getFilePath()).isNull();
            }

            @Test
            void toCanceledShouldAddEvent() {
                FileCopy fileCopy = TestFileCopy.inProgress();
                FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(fileCopy, FileCopyStatus.TRACKED);

                fileCopy.toCanceled();

                assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
            }
        }

        @Nested
        class ToTracked {

            @Test
            void toTrackedShouldThrowGivenTransitionFromInProgress() {
                FileCopy fileCopy = TestFileCopy.inProgress();

                assertThatThrownBy(fileCopy::toTracked)
                        .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                        .hasMessageContaining(fileCopy.getId().toString())
                        .hasMessageContaining(FileCopyStatus.IN_PROGRESS.toString())
                        .hasMessageContaining(FileCopyStatus.TRACKED.toString());
            }

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
        }

        @Nested
        class Enqueue {

            @Test
            void enqueueShouldAddEventGivenNotAlreadyEnqueued() {
                FileCopy fileCopy = TestFileCopy.tracked();
                var expectedEvent = new FileCopyEnqueuedEvent(
                        fileCopy.getId()
                );

                fileCopy.enqueue();

                assertThat(fileCopy.getDomainEvents())
                        .containsExactly(expectedEvent);
            }

            @Test
            void enqueueShouldDoNothingGivenAlreadyEnqueued() {
                FileCopy fileCopy = TestFileCopy.enqueued();
                FileCopyStatus initialStatus = fileCopy.getStatus();

                fileCopy.enqueue();

                assertThat(fileCopy.getStatus())
                        .isEqualTo(initialStatus);
                assertThat(fileCopy.getDomainEvents())
                        .isEmpty();
            }

            @Test
            void enqueueShouldTransitionFromFailedAndLoseFailedReason() {
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
            void enqueueShouldTransitionFromStoredUnverifiedAndKeepFilePath() {
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
            void enqueueShouldTransitionFromStoredVerifiedAndKeepFilePath() {
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
            void toInProgressShouldThrowGivenNullFilePath() {
                FileCopy fileCopy = TestFileCopy.enqueued();

                assertThatThrownBy(() -> fileCopy.toInProgress(null))
                        .isInstanceOf(NullPointerException.class)
                        .hasMessageContaining("filePath");
            }

            @Test
            void toInProgressShouldThrowGivenNotTransitioningFromEnqueued() {
                FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();

                assertThatThrownBy(() -> fileCopy.toInProgress("someFilePath"))
                        .isInstanceOf(InvalidFileCopyStatusTransitionException.class)
                        .hasMessageContaining(fileCopy.getId().toString())
                        .hasMessageContaining(FileCopyStatus.STORED_INTEGRITY_UNKNOWN.toString())
                        .hasMessageContaining(FileCopyStatus.IN_PROGRESS.toString());
            }

            @Test
            void toInProgressShouldChangeFileCopyStatus() {
                FileCopy fileCopy = TestFileCopy.enqueued();

                fileCopy.toInProgress("someFilePath");

                assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.IN_PROGRESS);
            }

            @Test
            void toInProgressShouldAddEvent() {
                FileCopy fileCopy = TestFileCopy.enqueued();
                var expectedEvent = new FileBackupStartedEvent(
                        fileCopy.getId(),
                        fileCopy.getNaturalId(),
                        "someFilePath"
                );

                fileCopy.toInProgress(expectedEvent.filePath());

                assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
            }

            @Test
            void toInProgressShouldDoNothingGivenAlreadyInProgress() {
                FileCopy fileCopy = TestFileCopy.inProgress();
                FileCopyStatus initialStatus = fileCopy.getStatus();
                String initialFilePath = fileCopy.getFilePath();

                fileCopy.toInProgress(initialFilePath + "/changed");

                assertThat(fileCopy.getStatus())
                        .isEqualTo(initialStatus);
                assertThat(fileCopy.getFilePath())
                        .isEqualTo(initialFilePath);
                assertThat(fileCopy.getDomainEvents())
                        .isEmpty();
            }
        }

        @Nested
        class ToStored {

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