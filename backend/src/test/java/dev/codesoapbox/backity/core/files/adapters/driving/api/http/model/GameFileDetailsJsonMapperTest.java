package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileDetailsJsonMapperTest {

    @Test
    void shouldMapToJson() {
        GameFileDetails domain = TestGameFileDetails.FULL_GAME_FILE_DETAILS.get();

        GameFileDetailsJson result = GameFileDetailsJsonMapper.INSTANCE.toJson(domain);

        var expectedResult = new GameFileDetailsJson(
                "acde26d7-33c7-42ee-be16-bca91a604b48",
                "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                new SourceFileDetailsJson(
                        "someSourceId",
                        "someOriginalGameTitle",
                        "someFileTitle",
                        "someVersion",
                        "someUrl",
                        "someOriginalFileName",
                        "5 KB"
                ),
                new BackupDetailsJson(
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