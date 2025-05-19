package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameGameContentDiscoveryStatusChangedEventHttpDtoMapperTest {

    private static final GameContentDiscoveryStatusHttpDtoMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryStatusHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        var domain = new GameContentDiscoveryStatus(new GameProviderId("someGameProviderId"), true);

        GameContentDiscoveryStatusHttpDto result = MAPPER.toDto(domain);

        assertThat(result.gameProviderId()).isEqualTo("someGameProviderId");
        assertThat(result.isInProgress()).isTrue();
    }
}