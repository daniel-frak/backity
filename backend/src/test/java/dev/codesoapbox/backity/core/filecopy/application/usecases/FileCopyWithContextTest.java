package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
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
            SourceFile sourceFile = TestSourceFile.gog();
            Game game = TestGame.any();
            BackupTarget backupTarget = TestBackupTarget.localFolder();
            assertThatThrownBy(() -> new FileCopyWithContext(null, sourceFile, game, backupTarget, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("fileCopy");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullSourceFile() {
            FileCopy fileCopy = TestFileCopy.tracked();
            Game game = TestGame.any();
            BackupTarget backupTarget = TestBackupTarget.localFolder();
            assertThatThrownBy(() -> new FileCopyWithContext(fileCopy, null, game, backupTarget, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("sourceFile");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullGame() {
            FileCopy fileCopy = TestFileCopy.tracked();
            SourceFile sourceFile = TestSourceFile.gog();
            BackupTarget backupTarget = TestBackupTarget.localFolder();
            assertThatThrownBy(() -> new FileCopyWithContext(fileCopy, sourceFile, null, backupTarget, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("game");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullBackupTarget() {
            FileCopy fileCopy = TestFileCopy.tracked();
            SourceFile sourceFile = TestSourceFile.gog();
            Game game = TestGame.any();
            assertThatThrownBy(() -> new FileCopyWithContext(fileCopy, sourceFile, game, null, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("backupTarget");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void shouldConstructWithNullProgress() {
            FileCopy fileCopy = TestFileCopy.tracked();
            SourceFile sourceFile = TestSourceFile.gog();
            Game game = TestGame.any();
            BackupTarget backupTarget = TestBackupTarget.localFolder();

            var result = new FileCopyWithContext(fileCopy, sourceFile, game, backupTarget, null);

            assertThat(result).isNotNull();
        }
    }
}