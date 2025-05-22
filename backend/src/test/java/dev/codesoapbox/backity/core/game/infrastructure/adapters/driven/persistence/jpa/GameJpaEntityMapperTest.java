package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameJpaEntityMapperTest {

    private final GameJpaEntityMapper mapper = Mappers.getMapper(GameJpaEntityMapper.class);

    @Test
    void shouldMapDomainToJpa() {
        Game domain = domain();

        GameJpaEntity result = mapper.toEntity(domain);

        GameJpaEntity expectedResult = jpa();
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameJpaEntity jpa() {
        return new GameJpaEntity(
                UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"),
                "Test Game",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    private Game domain() {
        return TestGame.any();
    }

    @Test
    void shouldMapJpaToDomain() {
        GameJpaEntity entity = jpa();

        Game result = mapper.toDomain(entity);

        Game expectedResult = domain();
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}