package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryStatusHttpDtoMapperTest {

    private static final FileDiscoveryStatusHttpDtoMapper MAPPER =
            Mappers.getMapper(FileDiscoveryStatusHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        var domain = new FileDiscoveryStatus("someSource", true);

        var result = MAPPER.toDto(domain);

        assertThat(result.source()).isEqualTo("someSource");
        assertThat(result.isInProgress()).isTrue();
    }
}