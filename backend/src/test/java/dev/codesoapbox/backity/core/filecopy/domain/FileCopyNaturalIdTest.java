package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyNaturalIdTest {

    @Test
    void shouldCreate() {
        var gameFileId = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
        var backupTargetId = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");

        var result = new FileCopyNaturalId(gameFileId, backupTargetId);

        assertThat(result).isNotNull();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void constructorShouldThrowGivenNullGameFileId() {
        var backupTargetId = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");

        assertThatThrownBy(() -> new FileCopyNaturalId(null, backupTargetId))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("gameFileId");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void constructorShouldThrowGivenNullBackupTargetId() {
        var gameFileId = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

        assertThatThrownBy(() -> new FileCopyNaturalId(gameFileId, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("backupTargetId");
    }
}