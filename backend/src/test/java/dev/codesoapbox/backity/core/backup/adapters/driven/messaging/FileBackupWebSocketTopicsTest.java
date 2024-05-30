package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupWebSocketTopicsTest {

    @Test
    void toStringShouldReturnValue() {
        String result = FileBackupWebSocketTopics.BACKUP_STARTED.toString();

        assertThat(result).isEqualTo("/topic/backups/started");
    }
}