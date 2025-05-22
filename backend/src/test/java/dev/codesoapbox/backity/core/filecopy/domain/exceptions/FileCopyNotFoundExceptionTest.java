package dev.codesoapbox.backity.core.filecopy.domain.exceptions;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyNotFoundExceptionTest {

    @Test
    void shouldGetMessageWithFileCopyId() {
        var id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
        var exception = new FileCopyNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find FileCopy with id=6df888e8-90b9-4df5-a237-0cba422c0310";
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldGetMessageWithGameFileIdAndBackupTargetId() {
        var gameFileId = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
        var backupTargetId = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");
        var exception = new FileCopyNotFoundException(gameFileId, backupTargetId);

        String result = exception.getMessage();

        var expectedResult = "Could not find FileCopy with gameFileId=acde26d7-33c7-42ee-be16-bca91a604b48, " +
                             "backupTargetId=3553a3c7-47a7-4f7a-8b47-75928bee37d0";
        assertThat(result).isEqualTo(expectedResult);
    }
}