package dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameJpaEntityMapperTest {

    private final GameJpaEntityMapper mapper = Mappers.getMapper(GameJpaEntityMapper.class);

    @Test
    void shouldMapToEntity() {
        Game domain = domainObject();

        GameJpaEntity result = mapper.toEntity(domain);

        GameJpaEntity expectedResult = entity();
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("dateCreated", "dateModified")
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameJpaEntity entity() {
        var expectedResult = new GameJpaEntity();
        expectedResult.setId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        expectedResult.setTitle("Test Game");

        return expectedResult;
    }

    private Game domainObject() {
        return TestGame.any();
    }

    @Test
    void shouldMapToDomain() {
        GameJpaEntity entity = entity();

        Game result = mapper.toDomain(entity);

        Game expectedResult = domainObject();
        assertThat(result).hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}