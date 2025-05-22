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
            var result = new FileCopy(FileCopyId.newInstance(), GameFileId.newInstance(),
                    BackupTargetId.newInstance(), FileBackupStatus.DISCOVERED,
                    null, null, null, null);

            assertThat(result).isNotNull();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullId() {
            GameFileId gameFileId = GameFileId.newInstance();
            BackupTargetId backupTargetId = BackupTargetId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    null,
                    gameFileId,
                    backupTargetId,
                    FileBackupStatus.DISCOVERED,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("id is marked non-null but is null");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullGameFileId() {
            FileCopyId id = FileCopyId.newInstance();
            BackupTargetId backupTargetId = BackupTargetId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    null,
                    backupTargetId,
                    FileBackupStatus.DISCOVERED,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("gameFileId is marked non-null but is null");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullBackupTargetId() {
            FileCopyId id = FileCopyId.newInstance();
            GameFileId gameFileId = GameFileId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    gameFileId,
                    null,
                    FileBackupStatus.DISCOVERED,
                    null,
                    null,
                    null,
                    null
            )).isInstanceOf(NullPointerException.class)
                    .hasMessage("backupTargetId is marked non-null but is null");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullStatus() {
            FileCopyId id = FileCopyId.newInstance();
            GameFileId gameFileId = GameFileId.newInstance();
            BackupTargetId backupTargetId = BackupTargetId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    gameFileId,
                    backupTargetId,
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
            GameFileId gameFileId = GameFileId.newInstance();
            BackupTargetId backupTargetId = BackupTargetId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    gameFileId,
                    backupTargetId,
                    FileBackupStatus.FAILED,
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
            GameFileId gameFileId = GameFileId.newInstance();
            BackupTargetId backupTargetId = BackupTargetId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    gameFileId,
                    backupTargetId,
                    FileBackupStatus.SUCCESS,
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
            GameFileId gameFileId = GameFileId.newInstance();
            BackupTargetId backupTargetId = BackupTargetId.newInstance();

            assertThatThrownBy(() -> new FileCopy(
                    id,
                    gameFileId,
                    backupTargetId,
                    FileBackupStatus.DISCOVERED,
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

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.DISCOVERED,
                    null,
                    null,
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
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
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.DISCOVERED,
                    null,
                    "someFilePath",
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
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
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.ENQUEUED,
                    null,
                    null,
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
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
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.ENQUEUED,
                    null,
                    "someFilePath",
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
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
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    null,
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldTransitionFromSuccessAndKeepFilePath() {
            FileCopy fileCopy = TestFileCopy.successful();

            fileCopy.toInProgress();

            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.IN_PROGRESS,
                    null,
                    "someFilePath",
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toInProgressShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.successful();
            FileBackupStartedEvent expectedEvent = fileBackupStartedEvent(fileCopy);

            fileCopy.toInProgress();

            assertThat(fileCopy.getStatus()).isEqualTo(FileBackupStatus.IN_PROGRESS);
            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        private FileBackupStartedEvent fileBackupStartedEvent(FileCopy fileCopy) {

            return new FileBackupStartedEvent(
                    fileCopy.getId(),
                    fileCopy.getGameFileId(),
                    fileCopy.getFilePath()
            );
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

            // @TODO These can be done with TestFileCopy builders
            var expectedResult = new FileCopy(
                    fileCopy.getId(),
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.SUCCESS,
                    null,
                    "someFilePath",
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
            );
            assertThat(fileCopy).usingRecursiveComparison()
                    .ignoringFields("domainEvents")
                    .isEqualTo(expectedResult);
        }

        @Test
        void toSuccessfulShouldAddEvent() {
            FileCopy fileCopy = TestFileCopy.inProgress();
            FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(fileCopy);

            fileCopy.toSuccessful("someFilePath");

            assertThat(fileCopy.getDomainEvents()).containsExactly(expectedEvent);
        }

        private FileBackupFinishedEvent fileBackupFinishedEvent(FileCopy fileCopy) {
            return new FileBackupFinishedEvent(fileCopy.getId(), fileCopy.getGameFileId());
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
                    fileCopy.getGameFileId(),
                    fileCopy.getBackupTargetId(),
                    FileBackupStatus.FAILED,
                    "someFailedReason",
                    "someFilePath",
                    fileCopy.getDateCreated(),
                    fileCopy.getDateModified()
            );
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
            return new FileBackupFailedEvent(fileCopy.getId(), fileCopy.getGameFileId(), failedReason);
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