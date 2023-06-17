package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaEntity;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.discovered;
import static org.assertj.core.api.Assertions.assertThat;

class GameFileDetailsDetailsMapperTestJpaEntity {

    private final GameFileDetailsJpaEntityMapper MAPPER = Mappers.getMapper(GameFileDetailsJpaEntityMapper.class);

    @Test
    void shouldMapToEntity() {
        GameFileDetails model = discovered().build();

        GameFileDetailsJpaEntity result = MAPPER.toEntity(model);

        GameJpaEntity gameJpaEntity = new GameJpaEntity();
        gameJpaEntity.setId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"));
        var expectedResult = new GameFileDetailsJpaEntity(
                UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                gameJpaEntity,
                new SourceFileDetailsJpaEntity(
                        "someSourceId",
                        "someOriginalGameTitle",
                        "someFileTitle",
                        "someVersion",
                        "someUrl",
                        "someOriginalFileName",
                        "5 KB"
                ),
                new BackupDetailsJpaEntity(
                        FileBackupStatus.DISCOVERED,
                        null,
                        null
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldMapToModel() {
        GameJpaEntity gameJpaEntity = new GameJpaEntity();
        gameJpaEntity.setId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"));

        var model = new GameFileDetailsJpaEntity(
                UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                gameJpaEntity,
                new SourceFileDetailsJpaEntity(
                        "someSourceId",
                        "someOriginalGameTitle",
                        "someFileTitle",
                        "someVersion",
                        "someUrl",
                        "someOriginalFileName",
                        "5 KB"
                ),
                new BackupDetailsJpaEntity(
                        FileBackupStatus.DISCOVERED,
                        null,
                        null
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );

        GameFileDetails result = MAPPER.toModel(model);

        var expectedResult = discovered().build();

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}