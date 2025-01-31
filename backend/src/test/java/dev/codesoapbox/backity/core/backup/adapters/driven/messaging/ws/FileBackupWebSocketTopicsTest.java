package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupWebSocketTopicsTest {

    @Test
    void wsDestinationShouldReturnValue() {
        String result = FileBackupWebSocketTopics.BACKUP_STARTED.wsDestination();

        assertThat(result).isEqualTo("/topic/backups/started");
    }

    @Test
    void toStringShouldReturnWsDestination() {
        assertThat(FileBackupWebSocketTopics.BACKUP_STARTED)
                .hasToString("/topic/backups/started");
    }
}