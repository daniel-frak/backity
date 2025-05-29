package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();

        var exception = new FileBackupFailedException(gameFile, fileCopy, null);

        assertThat(exception.getMessage())
                .isEqualTo("Could not back up game file acde26d7-33c7-42ee-be16-bca91a604b48" +
                           " (file copy 6df888e8-90b9-4df5-a237-0cba422c0310)");
    }

    @Test
    void shouldGetCause() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();
        var cause = new RuntimeException("test");

        var exception = new FileBackupFailedException(gameFile, fileCopy, cause);

        assertThat(exception.getCause()).isSameAs(cause);
    }
}