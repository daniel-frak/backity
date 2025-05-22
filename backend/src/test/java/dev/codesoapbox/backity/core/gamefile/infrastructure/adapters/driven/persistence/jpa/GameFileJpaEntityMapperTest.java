package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileJpaEntityMapperTest {

    private static final GameFileJpaEntityMapper MAPPER = Mappers.getMapper(GameFileJpaEntityMapper.class);

    @Test
    void shouldMapDomainToJpa() {
        GameFile domain = domain();

        GameFileJpaEntity result = MAPPER.toEntity(domain);

        GameFileJpaEntity expectedResult = jpa();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameFile domain() {
        return TestGameFile.gog();
    }

    private GameFileJpaEntity jpa() {
        return new GameFileJpaEntity(
                UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"),
                new FileSourceJpaEmbeddable(
                        "GOG",
                        "Game 1",
                        "Game 1 (Installer)",
                        "1.0.0",
                        "/downlink/some_game/some_file",
                        "game_1_installer.exe",
                        5120L
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    @Test
    void shouldMapJpaToDomain() {
        GameFileJpaEntity jpa = jpa();

        GameFile result = MAPPER.toDomain(jpa);

        GameFile expectedResult = domain();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}