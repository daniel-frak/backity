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
import dev.codesoapbox.backity.core.gamefile.domain.TestFileSource;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa.GameFileJpaEntityMapper;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@Transactional
abstract class GameWithFileCopiesReadModelJpaRepositoryAbstractIT {

    private static final LocalDateTime NOW = FakeTimeBeanConfig.FIXED_DATE_TIME;
    private static final LocalDate TODAY = NOW.toLocalDate();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);

    @Autowired
    private GameWithFileCopiesReadModelJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoSpyBean
    private GameWithFilesCopiesReadModelJpaEntityMapper entityMapper;

    @MockitoSpyBean
    private AuditingHandler auditingHandler;

    @BeforeEach
    void setUp() {
        populateDatabase();
    }

    private void populateDatabase() {
        doAnswer(inv -> inv)
                .when(auditingHandler).markCreated(any());
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

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" \n"})
    void shouldFindAllPaginatedGivenEmptyQuery(String searchQuery) {
        var pageable = new Pagination(0, 5);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);

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
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldNotThrowDuringCountQuery() {
        var pageable = new Pagination(0, 1);
        String searchQuery = null;

        assertThatCode(() -> repository.findAllPaginated(pageable, searchQuery))
                .doesNotThrowAnyException();
    }

    @Test
    void findAllPaginatedShouldFetchAllEntities() {
        var pageable = new Pagination(0, 1);

        List<GameWithFileCopiesReadModelJpaEntity> entities = interceptFetchedEntities();

        repository.findAllPaginated(pageable, null);
        entityManager.clear(); // Detach all entities to make sure there's no lazy loading

        GameWithFileCopiesReadModelJpaEntity firstGame = entities.getFirst();
        assertFetchedGameFiles(firstGame);
        assertFetchedFileCopies(firstGame);
    }

    private List<GameWithFileCopiesReadModelJpaEntity> interceptFetchedEntities() {
        List<GameWithFileCopiesReadModelJpaEntity> entities = new ArrayList<>();
        var captor = ArgumentCaptor.forClass(GameWithFileCopiesReadModelJpaEntity.class);
        when(entityMapper.toReadModel(captor.capture()))
                .thenAnswer(inv -> {
                    entities.add(captor.getValue());
                    return null;
                });
        return entities;
    }

    private void assertFetchedGameFiles(GameWithFileCopiesReadModelJpaEntity firstGame) {
        assertThatCode(() -> firstGame.getGameFilesWithCopies().getFirst())
                .doesNotThrowAnyException();
    }

    private void assertFetchedFileCopies(GameWithFileCopiesReadModelJpaEntity firstGame) {
        GameFileWithCopiesReadModelJpaEntity firstGameFile = firstGame.getGameFilesWithCopies().getFirst();
        assertThatCode(() -> firstGameFile.getFileCopies().getFirst())
                .doesNotThrowAnyException();
    }

    private void assertFoundInOrder(Page<GameWithFileCopiesReadModel> result, Page<GameWithFileCopiesReadModel> expectedResult) {
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expectedResult);
        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedResult.content());
    }

    @Test
    void findAllPaginatedShouldTokenizeEveryWordIfNotQuoted() {
        var pageable = new Pagination(0, 5);
        var searchQuery = "Test Game";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);

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
        assertFoundInOrder(result, expectedResult);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Test_Game", "100%", "Better\\Edition"})
    void findAllPaginatedShouldEscapeCharacters(String searchQuery) {
        var pageable = new Pagination(0, 5);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);

        Page<GameWithFileCopiesReadModel> expectedResult = new Page<>(List.of(
                TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                        .withValuesFrom(EXISTING_GAMES.GAME_2)
                        .build()
        ),
                5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenGameTitleCaseInsensitive() {
        var pageable = new Pagination(0, 5);
        var searchQuery = "\"" + EXISTING_GAMES.GAME_1.getTitle().toUpperCase() + "\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenPartOfGameTitleCaseInsensitive() {
        var pageable = new Pagination(0, 5);
        var partOfGameTitleQuery = "\"EST GAME\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, partOfGameTitleQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenFileTitle() {
        var pageable = new Pagination(0, 5);
        var searchQuery = "\"" + EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.getFileSource().fileTitle()
                .toUpperCase() + "\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenPartOfFileTitleCaseInsensitive() {
        var pageable = new Pagination(0, 5);
        var partOfFileTitleQuery = "\"1 (INSTALLER\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, partOfFileTitleQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenOriginalFileNameCaseInsensitive() {
        var pageable = new Pagination(0, 5);
        var searchQuery = "\"" + EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.getFileSource().originalFileName()
                .toUpperCase() + "\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenPartOfOriginalFileNameCaseInsensitive() {
        var pageable = new Pagination(0, 5);
        var partOfOriginalFileNameQuery = "\"1_INSTALLER.EX\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, partOfOriginalFileNameQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenOriginalGameTitleCaseInsensitive() {
        var pageable = new Pagination(0, 5);
        var searchQuery = "\"" + EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.getFileSource().originalGameTitle()
                .toUpperCase() + "\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void shouldFindAllPaginatedGivenPartOfOriginalGameTitleCaseInsensitive() {
        var pageable = new Pagination(0, 5);
        var partOfOriginalGameTitleSearchQuery = "\"RIGINAL GAME 1\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, partOfOriginalGameTitleSearchQuery);

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
                        .build()
        ), 5, 1, 1, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    @Test
    void findAllPaginatedShouldReturnEmptyGivenNothingMatches() {
        var pageable = new Pagination(0, 5);
        var searchQuery = "\"notMatchingAnything\"";

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pageable, searchQuery);
        Page<GameWithFileCopiesReadModel> expectedResult = new Page<>(emptyList(),
                5, 0, 0, 5, 0);
        assertFoundInOrder(result, expectedResult);
    }

    private static class EXISTING_GAMES {

        private static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = TestGame.anyBuilder()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle("Test Game 1001")
                .withDateCreated(TODAY.atStartOfDay())
                .build();

        public static final Game GAME_2 = TestGame.anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle("Test_Game 2 - 100% Better\\Edition") // [_%\] included to test escaping characters
                .withDateCreated(YESTERDAY.atStartOfDay())
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
                .fileSource(TestFileSource.minimalGogBuilder()
                        .originalGameTitle("Original Game 1 Title")
                        .fileTitle("Game 1 (Installer)")
                        .originalFileName("game_1_installer.exe")
                        .build())
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