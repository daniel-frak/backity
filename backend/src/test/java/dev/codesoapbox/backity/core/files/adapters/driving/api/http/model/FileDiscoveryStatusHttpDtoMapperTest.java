package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDiscoveryStatusHttpDtoMapperTest {

    private static final FileDiscoveryStatusHttpDtoMapper MAPPER =
            Mappers.getMapper(FileDiscoveryStatusHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        var domain = new FileDiscoveryStatus("someSource", true);

        var result = MAPPER.toDto(domain);

        assertEquals("someSource", result.getSource());
        assertTrue(result.isInProgress());
    }
}