package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryOutcome;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryResultJpaEntityMapperTest {

    private static final GameContentDiscoveryResultJpaEntityMapper MAPPER =
            Mappers.getMapper(GameContentDiscoveryResultJpaEntityMapper.class);

    @Test
    void shouldMapToEntity() {
        GameContentDiscoveryResult domain = domain();

        GameContentDiscoveryResultJpaEntity result = MAPPER.toEntity(domain);

        GameContentDiscoveryResultJpaEntity expectedResult = entity();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private GameContentDiscoveryResult domain() {
        return TestGameContentDiscoveryResult.gog();
    }

    private GameContentDiscoveryResultJpaEntity entity() {
        return new GameContentDiscoveryResultJpaEntity(
                "GOG",
                LocalDateTime.parse("2022-04-29T15:00:00"),
                LocalDateTime.parse("2022-04-29T16:00:00"),
                GameContentDiscoveryOutcome.SUCCESS,
                LocalDateTime.parse("2022-04-20T10:00:00"),
                5,
                70
        );
    }

    @Test
    void shouldMapToDomain() {
        GameContentDiscoveryResultJpaEntity entity = entity();

        GameContentDiscoveryResult result = MAPPER.toDomain(entity);

        GameContentDiscoveryResult expectedResult = domain();
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}