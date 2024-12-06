package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.successfulGameFile;
import static org.assertj.core.api.Assertions.*;

class GameFileTest {

    @Test
    void shouldEnqueue() {
        GameFile gameFile = discoveredGameFile().build();

        gameFile.enqueue();

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
    }

    @Test
    void shouldFailWithEvent() {
        GameFile gameFile = discoveredGameFile().build();
        String failedReason = "someFailedReason";
        FileBackupFailedEvent expectedEvent = fileBackupFailedEvent(gameFile, failedReason);

        gameFile.fail(failedReason);

        assertThat(gameFile.getFileBackup().getFailedReason()).isEqualTo(failedReason);
        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.FAILED);
        assertThat(gameFile.getDomainEvents()).containsExactly(expectedEvent);
    }

    private FileBackupFailedEvent fileBackupFailedEvent(GameFile gameFile, String failedReason) {
        return new FileBackupFailedEvent(gameFile.getId(), failedReason);
    }

    @Test
    void shouldMarkAsInProgressWithEvent() {
        GameFile gameFile = discoveredGameFile().build();
        GameProviderFile gameProviderFile = gameFile.getGameProviderFile();
        FileBackupStartedEvent expectedEvent = fileBackupStartedEvent(gameFile, gameProviderFile);

        gameFile.markAsInProgress();

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.IN_PROGRESS);
        assertThat(gameFile.getDomainEvents()).containsExactly(expectedEvent);
    }

    private FileBackupStartedEvent fileBackupStartedEvent(
            GameFile gameFile, GameProviderFile gameProviderFile) {
        return new FileBackupStartedEvent(
                gameFile.getId(),
                gameProviderFile.originalGameTitle(),
                gameProviderFile.fileTitle(),
                gameProviderFile.version(),
                gameProviderFile.originalFileName(),
                gameProviderFile.size(),
                gameFile.getFileBackup().getFilePath()
        );
    }

    @Test
    void shouldMarkAsDownloadedWithEvent() {
        GameFile gameFile = discoveredGameFile().build();
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
        GameFile gameFile = successfulGameFile().build();

        assertThatCode(gameFile::validateIsBackedUp)
                .doesNotThrowAnyException();
    }

    @Test
    void validateIsBackedUpShouldThrowGivenStatusIsNotSuccessful() {
        GameFile gameFile = discoveredGameFile().build();

        assertThatThrownBy(gameFile::validateIsBackedUp)
                .isInstanceOf(GameFileNotBackedUpException.class)
                .hasMessageContaining(gameFile.getId().toString());
    }

    @Test
    void shouldClearDomainEvents() {
        GameFile gameFile = discoveredGameFile().build();
        gameFile.markAsInProgress();

        gameFile.clearDomainEvents();

        assertThat(gameFile.getDomainEvents()).isEmpty();
    }
}