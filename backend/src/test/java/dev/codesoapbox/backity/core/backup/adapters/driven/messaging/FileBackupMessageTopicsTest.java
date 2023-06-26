package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupMessageTopicsTest {

    @Test
    void toStringShouldReturnValue() {
        String result = FileBackupMessageTopics.BACKUP_STARTED.toString();

        assertThat(result).isEqualTo("/topic/backups/started");
    }
}