package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryStatusChangedEventHttpDtoMapperTest {

    private static final FileDiscoveryStatusHttpDtoMapper MAPPER =
            Mappers.getMapper(FileDiscoveryStatusHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        var domain = new FileDiscoveryStatus("someGameProviderId", true);

        var result = MAPPER.toDto(domain);

        assertThat(result.gameProviderId()).isEqualTo("someGameProviderId");
        assertThat(result.isInProgress()).isTrue();
    }
}