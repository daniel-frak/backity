package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryOverview;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryOverviewHttpDtoMapperTest {

    private static final GameContentDiscoveryOverviewHttpDtoMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryOverviewHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        GameContentDiscoveryOverview domain = domain();

        GameContentDiscoveryOverviewHttpDto result = MAPPER.toDto(domain);

        GameContentDiscoveryOverviewHttpDto expectedResult = dto();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GameContentDiscoveryOverview domain() {
        return new GameContentDiscoveryOverview(
                new GameProviderId("someGameProviderId"),
                true,
                TestGameContentDiscoveryProgress.twentyFivePercentGog(),
                TestGameContentDiscoveryResult.gog()
        );
    }

    private GameContentDiscoveryOverviewHttpDto dto() {
        return new GameContentDiscoveryOverviewHttpDto(
                "someGameProviderId",
                true,
                new GameContentDiscoveryProgressHttpDto(
                        25,
                        10,
                        44,
                        55
                ),
                new GameContentDiscoveryResultHttpDto(
                        LocalDateTime.parse("2022-04-29T15:00:00"),
                        LocalDateTime.parse("2022-04-29T16:00:00"),
                        GameContentDiscoveryOutcomeHttpDto.SUCCESS,
                        LocalDateTime.parse("2022-04-20T10:00:00"),
                        5,
                        70
                )
        );
    }
}