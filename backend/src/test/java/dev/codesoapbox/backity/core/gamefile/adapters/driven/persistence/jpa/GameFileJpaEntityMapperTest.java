package dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class GameFileJpaEntityMapperTest {

    private static final GameFileJpaEntityMapper MAPPER = Mappers.getMapper(GameFileJpaEntityMapper.class);

    @Test
    void shouldMapToEntity() {
        GameFile model = domainModel();

        GameFileJpaEntity result = MAPPER.toEntity(model);

        var expectedResult = entity();

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameFile domainModel() {
        return discoveredGameFile().build();
    }

    private GameFileJpaEntity entity() {
        return new GameFileJpaEntity(
                UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"),
                new GameProviderFileJpaEntity(
                        "someGameProviderId",
                        "someOriginalGameTitle",
                        "someFileTitle",
                        "someVersion",
                        "someUrl",
                        "someOriginalFileName",
                        "5 KB"
                ),
                new FileBackupJpaEntity(
                        FileBackupStatus.DISCOVERED,
                        null,
                        null
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    @Test
    void shouldMapToModel() {
        var model = entity();

        GameFile result = MAPPER.toModel(model);

        var expectedResult = domainModel();

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}