package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyWithContextTest {

    @Nested
    class Creation {

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullFileCopy() {
            GameFile gameFile = TestGameFile.gog();
            Game game = TestGame.any();
            BackupTarget backupTarget = TestBackupTarget.localFolder();
            assertThatThrownBy(() -> new FileCopyWithContext(null, gameFile, game, backupTarget, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileCopy");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullGameFile() {
            FileCopy fileCopy = TestFileCopy.tracked();
            Game game = TestGame.any();
            BackupTarget backupTarget = TestBackupTarget.localFolder();
            assertThatThrownBy(() -> new FileCopyWithContext(fileCopy, null, game, backupTarget, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("gameFile");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullGame() {
            FileCopy fileCopy = TestFileCopy.tracked();
            GameFile gameFile = TestGameFile.gog();
            BackupTarget backupTarget = TestBackupTarget.localFolder();
            assertThatThrownBy(() -> new FileCopyWithContext(fileCopy, gameFile, null, backupTarget, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("game");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullBackupTarget() {
            FileCopy fileCopy = TestFileCopy.tracked();
            GameFile gameFile = TestGameFile.gog();
            Game game = TestGame.any();
            assertThatThrownBy(() -> new FileCopyWithContext(fileCopy, gameFile, game, null, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("backupTarget");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void shouldConstructWithNullProgress() {
            FileCopy fileCopy = TestFileCopy.tracked();
            GameFile gameFile = TestGameFile.gog();
            Game game = TestGame.any();
            BackupTarget backupTarget = TestBackupTarget.localFolder();

            var result = new FileCopyWithContext(fileCopy, gameFile, game, backupTarget, null);

            assertThat(result).isNotNull();
        }
    }
}