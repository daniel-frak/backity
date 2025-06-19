package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopyJpaEntityMapper;
import dev.codesoapbox.backity.core.game.application.readmodel.*;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa.GameFileJpaEntityMapper;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
abstract class GameWithFileCopiesReadModelJpaRepositoryAbstractIT {

    private static final LocalDateTime NOW = FakeTimeBeanConfig.FIXED_DATE_TIME;
    private static final LocalDate TODAY = NOW.toLocalDate();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);

    @Autowired
    private GameWithFileCopiesReadModelJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        populateDatabase();
    }

    private void populateDatabase() {
        for (Game game : EXISTING_GAMES.getAll()) {
            entityManager.persist(EXISTING_GAMES.MAPPER.toEntity(game));
        }
        for (GameFile gameFile : EXISTING_GAME_FILES.getAll()) {
            entityManager.persist(EXISTING_GAME_FILES.MAPPER.toEntity(gameFile));
        }
        for (FileCopy fileCopy : EXISTING_FILE_COPIES.getAll()) {
            entityManager.persist(EXISTING_FILE_COPIES.MAPPER.toEntity(fileCopy));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldFindAllPaginated() {
        Pagination pageable = new Pagination(0, 5);
        Page<GameWithFileCopiesReadModel> result = repository.findAll(pageable);

        Page<GameWithFileCopiesReadModel> expectedResult = new Page<>(List.of(
                TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                        .withValuesFrom(EXISTING_GAMES.GAME_1)
                        .withGameFilesWithCopies(List.of(
                                new GameFileWithCopiesReadModel(
                                        TestGameFileReadModel.from(EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1),
                                        List.of(
                                                TestFileCopyReadModel.from(EXISTING_FILE_COPIES
                                                        .DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1)
                                        )
                                )
                        ))
                        .build(),
                TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                        .withValuesFrom(EXISTING_GAMES.GAME_2)
                        .build()
        ), 5, 1, 2, 5, 0);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expectedResult);
        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
                        "dateCreated",
                        "dateModified",
                        "gameFilesWithCopies.gameFile.dateCreated",
                        "gameFilesWithCopies.gameFile.dateModified",
                        "gameFilesWithCopies.fileCopies.dateCreated",
                        "gameFilesWithCopies.fileCopies.dateModified"
                )
                .containsExactlyInAnyOrderElementsOf(expectedResult.content());
    }

    private static class EXISTING_GAMES {

        private static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = TestGame.anyBuilder()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle("Test Game 1")
                .build();

        public static final Game GAME_2 = TestGame.anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle("Test Game 2")
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_1, GAME_2);
        }
    }

    private static class EXISTING_GAME_FILES {

        private static final GameFileJpaEntityMapper MAPPER = Mappers.getMapper(GameFileJpaEntityMapper.class);

        public static final GameFile GOG_GAME_FILE_1_FOR_GAME_1 = TestGameFile.gogBuilder()
                .id(new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static List<GameFile> getAll() {
            return List.of(GOG_GAME_FILE_1_FOR_GAME_1);
        }
    }

    private static class EXISTING_FILE_COPIES {

        private static final FileCopyJpaEntityMapper MAPPER = Mappers.getMapper(FileCopyJpaEntityMapper.class);

        public static final FileCopy DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1 =
                TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("9fdad52f-b4a6-46bc-af6d-bf27f9661eae"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.getId(),
                                new BackupTargetId("f882cf23-35f9-4396-832d-bd08cd50e413")
                        ))
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static List<FileCopy> getAll() {
            return List.of(DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1);
        }
    }
}