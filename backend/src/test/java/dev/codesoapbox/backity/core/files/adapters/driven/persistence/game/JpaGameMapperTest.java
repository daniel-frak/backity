package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JpaGameMapperTest {

    private final JpaGameMapper mapper = Mappers.getMapper(JpaGameMapper.class);
    @Test
    void shouldMapToEntity() {
        var uuid = UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5");
        var title = "someTitle";
        var game = new Game(new GameId(uuid), title);

        var result = mapper.toEntity(game);

        var expectedResult = new JpaGame();
        expectedResult.setId(uuid);
        expectedResult.setTitle(title);

        assertThat(result).hasNoNullFieldsOrPropertiesExcept("dateCreated", "dateModified")
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnNullWhenGivenNull() {
        assertThat(mapper.toEntity(null))
                .isNull();
    }
}