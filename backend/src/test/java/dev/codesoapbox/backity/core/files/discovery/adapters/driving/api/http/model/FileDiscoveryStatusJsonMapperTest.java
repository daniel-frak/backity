package dev.codesoapbox.backity.core.files.discovery.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDiscoveryStatusJsonMapperTest {

    @Test
    void shouldMapToJson() {
        var domain = new FileDiscoveryStatus("someSource", true);

        var result = FileDiscoveryStatusJsonMapper.INSTANCE.toJson(domain);

        assertEquals("someSource", result.getSource());
        assertTrue(result.isInProgress());
    }
}