package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.backup.domain.TestFileCopyReplicationProgress;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDto;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyNaturalIdHttpDto;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesAndReplicationProgresses;
import dev.codesoapbox.backity.core.game.application.readmodel.GameFileWithCopiesReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.GameWithFileCopiesReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.TestFileCopyReadModel;
import dev.codesoapbox.backity.core.game.application.readmodel.TestGameFileReadModel;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileCopyStatusHttpDto;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileSourceHttpDto;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class GameWithFileCopiesReadModelHttpDtoMapperTest {

    private static final GameWithFileCopiesReadModelHttpDtoMapper MAPPER =
            Mappers.getMapper(GameWithFileCopiesReadModelHttpDtoMapper.class);

    private static final FileCopyId IN_PROGRESS_FILE_COPY_ID =
            new FileCopyId("d6c81f47-e2b6-424b-b997-0dad6aa372c7");
    private static final String GAME_ID = "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5";
    private static final String GAME_FILE_ID = "acde26d7-33c7-42ee-be16-bca91a604b48";
    private static final String FILE_COPY_ID = "6df888e8-90b9-4df5-a237-0cba422c0310";
    private static final String BACKUP_TARGET_ID = "eda52c13-ddf7-406f-97d9-d3ce2cab5a76";

    @Test
    void shouldMapDomainToDto() {
        GameWithFileCopiesAndReplicationProgresses domain = domain();

        GameWithFileCopiesHttpDto result = MAPPER.toDto(domain);

        GameWithFileCopiesHttpDto expectedResult = dto();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private GameWithFileCopiesAndReplicationProgresses domain() {
        return new GameWithFileCopiesAndReplicationProgresses(
                new GameWithFileCopiesReadModel(
                        GAME_ID,
                        "Test Game",
                        List.of(
                                new GameFileWithCopiesReadModel(
                                        TestGameFileReadModel.from(TestGameFile.gogBuilder()
                                                .gameId(new GameId(
                                                        GAME_ID))
                                                .build()),
                                        List.of(
                                                TestFileCopyReadModel.from(TestFileCopy.tracked()),
                                                TestFileCopyReadModel.from(TestFileCopy.inProgressBuilder()
                                                        .id(IN_PROGRESS_FILE_COPY_ID)
                                                        .build()),
                                                TestFileCopyReadModel.from((TestFileCopy.failedWithoutFilePath())
                                                )
                                        )
                                )
                        )
                ),
                List.of(
                        TestFileCopyReplicationProgress.twentyFivePercentBuilder()
                                .withFileCopyId(IN_PROGRESS_FILE_COPY_ID)
                                .build()
                )
        );
    }

    private GameWithFileCopiesHttpDto dto() {
        return new GameWithFileCopiesHttpDto(
                GAME_ID,
                "Test Game",
                singletonList(
                        new GameFileWithCopiesHttpDto(
                                new GameFileHttpDto(
                                        GAME_FILE_ID,
                                        GAME_ID,
                                        new FileSourceHttpDto(
                                                "GOG",
                                                "Game 1",
                                                "Game 1 (Installer)",
                                                "1.0.0",
                                                "/downlink/some_game/some_file",
                                                "game_1_installer.exe",
                                                "5 KB"
                                        ),
                                        LocalDateTime.parse("2022-04-29T14:15:53"),
                                        LocalDateTime.parse("2023-04-29T14:15:53")
                                ),
                                List.of(
                                        new FileCopyWithProgressHttpDto(
                                                fileCopyDto(
                                                        FILE_COPY_ID,
                                                        FileCopyStatusHttpDto.TRACKED,
                                                        null, null),
                                                null
                                        ),
                                        new FileCopyWithProgressHttpDto(
                                                fileCopyDto(
                                                        IN_PROGRESS_FILE_COPY_ID.toString(),
                                                        FileCopyStatusHttpDto.IN_PROGRESS,
                                                        "someFilePath", null),
                                                new ProgressHttpDto(25, 10)
                                        ),
                                        new FileCopyWithProgressHttpDto(
                                                fileCopyDto(
                                                        FILE_COPY_ID,
                                                        FileCopyStatusHttpDto.FAILED,
                                                        null, "someFailedReason"),
                                                null
                                        )
                                )
                        )
                )
        );
    }

    private FileCopyHttpDto fileCopyDto(String id, FileCopyStatusHttpDto status, String filePath, String failedReason) {
        return new FileCopyHttpDto(
                id,
                new FileCopyNaturalIdHttpDto(
                        GAME_FILE_ID,
                        BACKUP_TARGET_ID
                ),
                status,
                failedReason,
                filePath,
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    @Test
    void toDtoShouldReturnNullGivenNull() {
        assertThat(MAPPER.toDto((GameWithFileCopiesReadModel) null, emptyList()))
                .isNull();
    }
}