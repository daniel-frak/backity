package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileBackupWebSocketTopicsTest {

    @Nested
    class WsDestination {

        @Test
        void shouldReturnValue() {
            String result = FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination();

            assertThat(result).isEqualTo("/topic/backups/status-changed");
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnWsDestination() {
            assertThat(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED)
                    .hasToString("/topic/backups/status-changed");
        }
    }
}