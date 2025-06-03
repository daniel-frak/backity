package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupWebSocketTopicsTest {

    @Test
    void wsDestinationShouldReturnValue() {
        String result = FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination();

        assertThat(result).isEqualTo("/topic/backups/status-changed");
    }

    @Test
    void toStringShouldReturnWsDestination() {
        assertThat(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED)
                .hasToString("/topic/backups/status-changed");
    }
}