package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.full;
import static org.assertj.core.api.Assertions.assertThat;

class GameFileDetailsMessageMapperTest {

    private static final GameFileDetailsMessageMapper MAPPER = Mappers.getMapper(GameFileDetailsMessageMapper.class);

    @Test
    void shouldMapToMessage() {
        GameFileDetails domain = full().build();

        GameFileDetailsMessage result = MAPPER.toMessage(domain);

        var expectedResult = new GameFileDetailsMessage(
                "acde26d7-33c7-42ee-be16-bca91a604b48",
                "1eec1c19-25bf-4094-b926-84b5bb8fa281",
                new SourceFileDetailsMessage(
                        "someSourceId",
                        "someOriginalGameTitle",
                        "someFileTitle",
                        "someVersion",
                        "someUrl",
                        "someOriginalFileName",
                        "5 KB"
                ),
                new BackupDetailsMessage(
                        FileBackupStatus.DISCOVERED,
                        "someFailedReason",
                        "someFilePath"
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );

        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}