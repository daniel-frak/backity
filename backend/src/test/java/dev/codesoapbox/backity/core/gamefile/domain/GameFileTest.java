package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class GameFileTest {

    @Test
    void shouldCreateForGameAndFileSource() {
        Game game = TestGame.any();
        FileSource fileSource = TestFileSource.minimalGog();

        GameFile result = GameFile.createFor(game, fileSource);

        GameFile expectedResult = TestGameFile.discoveredBuilder()
                .id(result.getId())
                .gameId(game.getId())
                .fileSource(fileSource)
                .fileBackup(TestFileBackup.discovered())
                .dateCreated(null)
                .dateModified(null)
                .build();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldMarkAsEnqueued() {
        GameFile gameFile = TestGameFile.discovered();

        gameFile.markAsEnqueued();

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
    }

    @Test
    void shouldMarkAsFailedWithEvent() {
        GameFile gameFile = TestGameFile.discovered();
        String failedReason = "someFailedReason";
        FileBackupFailedEvent expectedEvent = fileBackupFailedEvent(gameFile, failedReason);

        gameFile.markAsFailed(failedReason);

        assertThat(gameFile.getFileBackup().getFailedReason()).isEqualTo(failedReason);
        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.FAILED);
        assertThat(gameFile.getDomainEvents()).containsExactly(expectedEvent);
    }

    private FileBackupFailedEvent fileBackupFailedEvent(GameFile gameFile, String failedReason) {
        return new FileBackupFailedEvent(gameFile.getId(), failedReason);
    }

    @Test
    void shouldMarkAsInProgressWithEvent() {
        GameFile gameFile = TestGameFile.discovered();
        FileBackupStartedEvent expectedEvent = fileBackupStartedEvent(gameFile);

        gameFile.markAsInProgress();

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.IN_PROGRESS);
        assertThat(gameFile.getDomainEvents()).containsExactly(expectedEvent);
    }

    private FileBackupStartedEvent fileBackupStartedEvent(GameFile gameFile) {
        FileSource fileSource = gameFile.getFileSource();
        FileBackup fileBackup = gameFile.getFileBackup();

        return new FileBackupStartedEvent(
                gameFile.getId(),
                fileSource.originalGameTitle(),
                fileSource.fileTitle(),
                fileSource.version(),
                fileSource.originalFileName(),
                fileSource.size(),
                fileBackup.getFilePath()
        );
    }

    @Test
    void shouldMarkAsDownloadedWithEvent() {
        GameFile gameFile = TestGameFile.discovered();
        FileBackupFinishedEvent expectedEvent = fileBackupFinishedEvent(gameFile);

        gameFile.markAsDownloaded("someFilePath");

        assertThat(gameFile.getFileBackup().getFilePath()).isEqualTo("someFilePath");
        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.SUCCESS);
        assertThat(gameFile.getDomainEvents()).containsExactly(expectedEvent);
    }

    private FileBackupFinishedEvent fileBackupFinishedEvent(GameFile gameFile) {
        return new FileBackupFinishedEvent(gameFile.getId());
    }

    @Test
    void validateIsBackedUpShouldDoNothingGivenStatusIsSuccessful() {
        GameFile gameFile = TestGameFile.successful();

        assertThatCode(gameFile::validateIsBackedUp)
                .doesNotThrowAnyException();
    }

    @Test
    void validateIsBackedUpShouldThrowGivenStatusIsNotSuccessful() {
        GameFile gameFile = TestGameFile.discovered();

        assertThatThrownBy(gameFile::validateIsBackedUp)
                .isInstanceOf(GameFileNotBackedUpException.class)
                .hasMessageContaining(gameFile.getId().toString());
    }

    @Test
    void shouldClearDomainEvents() {
        GameFile gameFile = TestGameFile.discovered();
        gameFile.markAsInProgress();

        gameFile.clearDomainEvents();

        assertThat(gameFile.getDomainEvents()).isEmpty();
    }

    @Test
    void getDomainEventsShouldReturnUnmodifiableList() {
        GameFile gameFile = TestGameFile.discovered();

        List<DomainEvent> result = gameFile.getDomainEvents();

        assertThatThrownBy(result::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }
}