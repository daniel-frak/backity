package dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameJpaEntityMapperTest {

    private final GameJpaEntityMapper mapper = Mappers.getMapper(GameJpaEntityMapper.class);

    @Test
    void shouldMapToEntity() {
        var uuid = UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        var title = "someTitle";
        var domain = new Game(new GameId(uuid), title);

        var result = mapper.toEntity(domain);

        var expectedResult = new GameJpaEntity();
        expectedResult.setId(uuid);
        expectedResult.setTitle(title);

        assertThat(result).hasNoNullFieldsOrPropertiesExcept("dateCreated", "dateModified")
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void toEntityShouldReturnNullWhenGivenNull() {
        assertThat(mapper.toEntity(null))
                .isNull();
    }

    @Test
    void shouldMapToDomain() {
        var uuid = UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        var title = "someTitle";
        var entity = new GameJpaEntity();
        entity.setId(uuid);
        entity.setTitle(title);

        var result = mapper.toDomain(entity);

        var expectedResult = new Game(new GameId(uuid), title);

        assertThat(result).hasNoNullFieldsOrProperties()
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}