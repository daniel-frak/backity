package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.application.GameWithFiles;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameWithFilesJsonMapperTest {

    private static final GameWithFilesJsonMapper MAPPER = Mappers.getMapper(GameWithFilesJsonMapper.class);

    @Test
    void shouldMapToDto() {
        GameId id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        var model = new GameWithFiles(
                new Game(id, "Test Game"),
                singletonList(
                        new GameFileVersionBackup(
                                1L,
                                "someSource",
                                "someUrl",
                                "someTitle",
                                "someOriginalFileName",
                                "someFilePath",
                                "someGameTitle",
                                id.value().toString(),
                                "someVersion",
                                "100 KB",
                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                FileBackupStatus.DISCOVERED,
                                "someFailReason"
                        )
                )
        );

        GameWithFilesJson result = MAPPER.toDto(model);

        var expectedResult = new GameWithFilesJson(
                id.value().toString(),
                "Test Game",
                singletonList(
                        new GameFileVersionBackupJson(
                                1L,
                                "someSource",
                                "someUrl",
                                "someTitle",
                                "someOriginalFileName",
                                "someFilePath",
                                "someGameTitle",
                                id.value().toString(),
                                "someVersion",
                                "100 KB",
                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                FileBackupStatus.DISCOVERED,
                                "someFailReason"
                        )
                )
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void toDtoShouldReturnNullGivenNull() {
        assertThat(MAPPER.toDto(null))
                .isNull();
    }
}