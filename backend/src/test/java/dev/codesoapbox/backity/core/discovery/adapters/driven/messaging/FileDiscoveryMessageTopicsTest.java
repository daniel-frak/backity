package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryMessageTopicsTest {

    @Test
    void toStringShouldReturnValue() {
        String result = FileDiscoveryMessageTopics.FILE_DISCOVERED.toString();

        assertThat(result).isEqualTo("/topic/file-discovery/file-discovered");
    }
}