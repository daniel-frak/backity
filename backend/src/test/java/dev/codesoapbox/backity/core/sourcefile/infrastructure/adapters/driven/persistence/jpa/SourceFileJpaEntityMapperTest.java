package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileJpaEntityMapperTest {

    private static final SourceFileJpaEntityMapper MAPPER = Mappers.getMapper(SourceFileJpaEntityMapper.class);

    @Test
    void shouldMapDomainToJpa() {
        SourceFile domain = domain();

        SourceFileJpaEntity result = MAPPER.toEntity(domain);

        SourceFileJpaEntity expectedResult = jpa();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private SourceFile domain() {
        return TestSourceFile.gog();
    }

    private SourceFileJpaEntity jpa() {
        return new SourceFileJpaEntity(
                UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"),
                "GOG",
                "Game 1",
                "Game 1 (Installer)",
                "1.0.0",
                "/downlink/some_game/some_file",
                "game_1_installer.exe",
                5120L,
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    @Test
    void shouldMapJpaToDomain() {
        SourceFileJpaEntity jpa = jpa();

        SourceFile result = MAPPER.toDomain(jpa);

        SourceFile expectedResult = domain();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}