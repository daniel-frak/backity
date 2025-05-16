package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameProviderFileUrlEmptyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameFileTest {

    @Test
    void shouldAssociateGameAndGameProviderFile() {
        Game game = TestGame.any();
        GameProviderFile gameProviderFile = TestGameProviderFile.minimalGog();

        GameFile result = GameFile.associate(game, gameProviderFile);

        GameFile expectedResult = TestGameFile.discoveredBuilder()
                .id(result.getId())
                .gameId(game.getId())
                .gameProviderFile(gameProviderFile)
                .fileBackup(TestFileBackup.discovered())
                .dateCreated(null)
                .dateModified(null)
                .build();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldEnqueue() {
        GameFile gameFile = TestGameFile.discovered();

        gameFile.enqueue();

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
    }

    @Test
    void shouldFailWithEvent() {
        GameFile gameFile = TestGameFile.discovered();
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
        GameFile gameFile = TestGameFile.discovered();
        FileBackupStartedEvent expectedEvent = fileBackupStartedEvent(gameFile);

        gameFile.markAsInProgress();

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.IN_PROGRESS);
        assertThat(gameFile.getDomainEvents()).containsExactly(expectedEvent);
    }

    private FileBackupStartedEvent fileBackupStartedEvent(GameFile gameFile) {
        GameProviderFile gameProviderFile = gameFile.getGameProviderFile();
        FileBackup fileBackup = gameFile.getFileBackup();

        return new FileBackupStartedEvent(
                gameFile.getId(),
                gameProviderFile.originalGameTitle(),
                gameProviderFile.fileTitle(),
                gameProviderFile.version(),
                gameProviderFile.originalFileName(),
                gameProviderFile.size(),
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
    void validateReadyForDownloadShouldDoNothingGivenTrue() {
        GameFile gameFile = TestGameFile.discovered();

        assertThatCode(gameFile::validateReadyForDownload)
                .doesNotThrowAnyException();
    }

    @Test
    void validateReadyForDownloadShouldDoNothingGivenGameProviderFileUrlIsBlank() {
        GameFile gameFile = aGameFileWithABlankUrl();

        assertThatThrownBy(gameFile::validateReadyForDownload)
                .isInstanceOf(GameProviderFileUrlEmptyException.class)
                .hasMessageContaining(gameFile.getId().toString());
    }

    private GameFile aGameFileWithABlankUrl() {
        return TestGameFile.discoveredBuilder()
                .gameProviderFile(TestGameProviderFile.minimalGogBuilder()
                        .url(" ")
                        .build())
                .build();
    }
}