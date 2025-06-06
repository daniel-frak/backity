package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryProgress;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryStatusChangedEventHttpDtoMapperTest {

    private static final GameContentDiscoveryStatusHttpDtoMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryStatusHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GameContentDiscoveryStatus domain = domain();

        GameContentDiscoveryStatusHttpDto result = MAPPER.toDto(domain);

        GameContentDiscoveryStatusHttpDto expectedResult = dto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GameContentDiscoveryStatus domain() {
        return new GameContentDiscoveryStatus(
                new GameProviderId("someGameProviderId"),
                true,
                TestGameContentDiscoveryProgress.twentyFivePercentGog()
        );
    }

    private GameContentDiscoveryStatusHttpDto dto() {
        return new GameContentDiscoveryStatusHttpDto(
                "someGameProviderId",
                true,
                new ProgressHttpDto(
                        25,
                        10
                )
        );
    }
}