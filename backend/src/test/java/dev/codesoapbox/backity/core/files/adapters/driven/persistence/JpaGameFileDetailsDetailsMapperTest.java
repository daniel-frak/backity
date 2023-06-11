package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.JpaGame;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JpaGameFileDetailsDetailsMapperTest {

    private final JpaGameFileDetailsMapper MAPPER = Mappers.getMapper(JpaGameFileDetailsMapper.class);

    @Test
    void shouldMapToEntity() {
        GameFileDetails model = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        JpaGameFileDetails result = MAPPER.toEntity(model);

        JpaGame jpaGame = new JpaGame();
        jpaGame.setId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"));
        var expectedResult = new JpaGameFileDetails(
                UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                jpaGame,
                new JpaSourceFileDetails(
                        "someSourceId1",
                        "someOriginalGameTitle1",
                        "someFileTitle1",
                        "someVersion1",
                        "someUrl1",
                        "someOriginalFileName1",
                        "5 KB"
                ),
                new JpaBackupDetails(
                        FileBackupStatus.ENQUEUED,
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
        JpaGame jpaGame = new JpaGame();
        jpaGame.setId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"));

        var model = new JpaGameFileDetails(
                UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                jpaGame,
                new JpaSourceFileDetails(
                        "someSourceId1",
                        "someOriginalGameTitle1",
                        "someFileTitle1",
                        "someVersion1",
                        "someUrl1",
                        "someOriginalFileName1",
                        "5 KB"
                ),
                new JpaBackupDetails(
                        FileBackupStatus.ENQUEUED,
                        null,
                        null
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );

        GameFileDetails result = MAPPER.toModel(model);

        var expectedResult = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}