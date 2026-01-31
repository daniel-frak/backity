package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopyJpaEntityMapper;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.TestGameWithFileCopiesSearchFilter;
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

    private static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;
    private static final LocalDate TODAY = NOW.toLocalDate();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TWO_DAYS_AGO = TODAY.minusDays(2);

    private static final Pagination EVERYTHING_ON_ONE_PAGE = new Pagination(0, 99);

    private static final GameWithFileCopiesReadModel GAME_1_READ_MODEL =
            TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                    .withValuesFrom(ExistingGames.GAME_1)
                    .withGameFilesWithCopies(List.of(
                            new GameFileWithCopiesReadModel(
                                    TestGameFileReadModel.from(ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_1),
                                    List.of(
                                            TestFileCopyReadModel.from(ExistingFileCopies
                                                    .TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1_FOR_GAME_1),
                                            // There used to be a bug where pagination would be incorrect due to
                                            // counting FileCopies instead of Games.
                                            // Adding the second FileCopy here helps to verify that the bug is fixed.
                                            TestFileCopyReadModel.from(ExistingFileCopies
                                                    .TRACKED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1_FOR_GAME_1)
                                    )
                            ),
                            // There used to be a bug where pagination would be incorrect due to
                            // counting GameFiles instead of Games.
                            // Adding the second GameFile here helps to verify that the bug is fixed.
                            new GameFileWithCopiesReadModel(
                                    TestGameFileReadModel.from(ExistingGameFiles.GOG_GAME_FILE_2_FOR_GAME_1),
                                    List.of(
                                            TestFileCopyReadModel.from(ExistingFileCopies
                                                    .TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2_FOR_GAME_1)
                                    )
                            )
                    ))
                    .build();

    private static final GameWithFileCopiesReadModel GAME_2_READ_MODEL =
            TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                    .withValuesFrom(ExistingGames.GAME_2)
                    .withGameFilesWithCopies(List.of(
                            new GameFileWithCopiesReadModel(
                                    TestGameFileReadModel.from(ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_2),
                                    List.of(
                                            TestFileCopyReadModel.from(ExistingFileCopies
                                                    .ENQUEUED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1_FOR_GAME_2)
                                    )
                            )
                    ))
                    .build();

    private static final GameWithFileCopiesReadModel GAME_3_READ_MODEL =
            TestGameWithFileCopiesReadModel.withNoGameFilesBuilder()
                    .withValuesFrom(ExistingGames.GAME_3)
                    .withGameFilesWithCopies(List.of(
                            // This game has a single GameFile with no FileCopies.
                            // We want to make sure it's still returned from queries.
                            new GameFileWithCopiesReadModel(
                                    TestGameFileReadModel.from(ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_3),
                                    emptyList()
                            )
                    ))
                    .build();

    @Autowired
    private GameWithFileCopiesReadModelJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameWithFilesCopiesReadModelJpaEntityMapper entityMapperSpy;

    @Autowired
    private AuditingHandler auditingHandlerSpy;

    @BeforeEach
    void setUp() {
        populateDatabase();
    }

    private void populateDatabase() {
        // Prevent Spring Data JPA auditing from overwriting preset @CreatedDate/@LastModifiedDate values
        // during test data setup:
        doAnswer(inv -> inv)
                .when(auditingHandlerSpy).markCreated(any());

        for (Game game : ExistingGames.getAll()) {
            entityManager.persist(ExistingGames.MAPPER.toEntity(game));
        }
        for (GameFile gameFile : ExistingGameFiles.getAll()) {
            entityManager.persist(ExistingGameFiles.MAPPER.toEntity(gameFile));
        }
        for (FileCopy fileCopy : ExistingFileCopies.getAll()) {
            entityManager.persist(ExistingFileCopies.MAPPER.toEntity(fileCopy));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            " \n", // Blank (blank search query)
            "\"\"", // "" (empty token list)
            "\"\t\""// "  " (blank token)
    }
    )
    void findAllPaginatedShouldReturnGamesGivenEmptyQueryAndNoOtherFilters(String searchQuery) {
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent =
                List.of(GAME_1_READ_MODEL, GAME_2_READ_MODEL, GAME_3_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    private void assertContentContainsExactlyInAnyOrder(Page<GameWithFileCopiesReadModel> result,
                                                        List<GameWithFileCopiesReadModel> expectedContent) {
        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedContent);
    }

    @Test
    void findAllPaginatedShouldReturnGamesOrderedByCreatedAt() {
        var filter = emptyFilter();

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent =
                List.of(GAME_1_READ_MODEL, GAME_2_READ_MODEL, GAME_3_READ_MODEL);
        assertContentContainsExactlyInOrder(result, expectedContent);
    }

    private GameWithFileCopiesSearchFilter emptyFilter() {
        return new GameWithFileCopiesSearchFilter(null, null);
    }

    private void assertContentContainsExactlyInOrder(Page<GameWithFileCopiesReadModel> result,
                                                     List<GameWithFileCopiesReadModel> expectedContent) {
        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(expectedContent);
    }

    @Test
    void findAllPaginatedShouldProperlyPaginate() {
        Pagination pagination = new Pagination(0, 2);
        var filter = emptyFilter();

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pagination, filter);

        Page<GameWithFileCopiesReadModel> expectedResult = new Page<>(
                List.of(GAME_1_READ_MODEL, GAME_2_READ_MODEL),
                2,
                3,
                pagination
        );
        assertPaginationInformationIsCorrect(result, expectedResult);
        assertContentContainsExactlyInAnyOrder(result, expectedResult.content());
    }

    private void assertPaginationInformationIsCorrect(Page<GameWithFileCopiesReadModel> result,
                                                      Page<GameWithFileCopiesReadModel> expectedResult) {
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expectedResult);
    }

    @Test
    void findAllPaginatedShouldFilterByFileCopyStatus() {
        var filter = new GameWithFileCopiesSearchFilter(null, FileCopyStatus.ENQUEUED);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_2_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFetchAllEntities() {
        Pagination pagination = new Pagination(0, 1);
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(null);

        List<GameWithFileCopiesReadModelJpaEntity> entities = interceptFetchedEntities();

        repository.findAllPaginated(pagination, filter);
        entityManager.clear(); // Detach all entities to make sure there's no lazy loading

        GameWithFileCopiesReadModelJpaEntity firstGame = entities.getFirst();
        assertFetchedGameFiles(firstGame);
        assertFetchedFileCopies(firstGame);
    }

    private List<GameWithFileCopiesReadModelJpaEntity> interceptFetchedEntities() {
        List<GameWithFileCopiesReadModelJpaEntity> entities = new ArrayList<>();
        var captor = ArgumentCaptor.forClass(GameWithFileCopiesReadModelJpaEntity.class);
        when(entityMapperSpy.toReadModel(captor.capture()))
                .thenAnswer(_ -> {
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

    @Test
    void findAllPaginatedShouldTokenizeEveryWordIfNotQuoted() {
        var searchQuery = "Test Game";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .contains(GAME_2_READ_MODEL); // Matched even though it has "TEST_GAME"
    }

    @ParameterizedTest
    @ValueSource(strings = {"Test_Game", "100%", "Better\\Edition"})
    void findAllPaginatedShouldEscapeCharacters(String searchQuery) {
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_2_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByFullGameTitleCaseInsensitive() {
        var searchQuery = "\"" + ExistingGames.GAME_1.getTitle().toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfGameTitleCaseInsensitive() {
        var partOfGameTitleQuery = "\"EST GAME 1\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfGameTitleQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByFullFileTitleCaseInsensitive() {
        var searchQuery = "\"" + ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_1.getFileSource().fileTitle()
                .toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfFileTitleCaseInsensitive() {
        var partOfFileTitleQuery = "\"1 (INSTALLER\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfFileTitleQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByFullOriginalFileNameCaseInsensitive() {
        var searchQuery = "\"" + ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_1.getFileSource().originalFileName()
                .toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfOriginalFileNameCaseInsensitive() {
        var partOfOriginalFileNameQuery = "\"1_INSTALLER.EX\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfOriginalFileNameQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByFullOriginalGameTitleCaseInsensitive() {
        var searchQuery = "\"" + ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_1.getFileSource().originalGameTitle()
                .toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfOriginalGameTitleCaseInsensitive() {
        var partOfOriginalGameTitleSearchQuery = "\"RIGINAL GAME 1\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfOriginalGameTitleSearchQuery);

        Page<GameWithFileCopiesReadModel> result =
                repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        List<GameWithFileCopiesReadModel> expectedContent = List.of(GAME_1_READ_MODEL);
        assertContentContainsExactlyInAnyOrder(result, expectedContent);
    }

    @Test
    void findAllPaginatedShouldReturnEmptyListGivenNothingMatches() {
        var searchQuery = "\"notMatchingAnything\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertContentIsEmpty(result);
    }

    private void assertContentIsEmpty(Page<GameWithFileCopiesReadModel> result) {
        assertContentContainsExactlyInAnyOrder(result, emptyList());
    }

    private static class ExistingGames {

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

        public static final Game GAME_3 = TestGame.anyBuilder()
                .withId(new GameId("0154e14b-b531-4748-9943-9b33ef596c2f"))
                .withTitle("Test Game 3")
                .withDateCreated(TWO_DAYS_AGO.atStartOfDay())
                .build();

        private static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static List<Game> getAll() {
            return List.of(GAME_1, GAME_2, GAME_3);
        }
    }

    private static class ExistingGameFiles {

        public static final GameFile GOG_GAME_FILE_1_FOR_GAME_1 = TestGameFile.gogBuilder()
                .id(new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(ExistingGames.GAME_1.getId())
                .fileSource(TestFileSource.minimalGogBuilder()
                        .originalGameTitle("Original Game 1 Title")
                        .fileTitle("Game 1 (Installer)")
                        .originalFileName("game_1_installer.exe")
                        .build())
                .build();

        public static final GameFile GOG_GAME_FILE_2_FOR_GAME_1 = TestGameFile.gogBuilder()
                .id(new GameFileId("119c7d15-4d83-46d0-9fd2-511f3abae641"))
                .gameId(ExistingGames.GAME_1.getId())
                .fileSource(TestFileSource.minimalGogBuilder()
                        .originalGameTitle("Original Game 1 Title")
                        .fileTitle("Game 1 (Patch)")
                        .originalFileName("game_1_patch.exe")
                        .build())
                .build();

        public static final GameFile GOG_GAME_FILE_1_FOR_GAME_2 = TestGameFile.gogBuilder()
                .id(new GameFileId("533f6571-d125-4a7e-a2f2-dc066e8ce538"))
                .gameId(ExistingGames.GAME_2.getId())
                .fileSource(TestFileSource.minimalGogBuilder()
                        .originalGameTitle("Original Game 2 Title")
                        .fileTitle("Game 2 File")
                        .originalFileName("game_2.exe")
                        .build())
                .build();

        public static final GameFile GOG_GAME_FILE_1_FOR_GAME_3 = TestGameFile.gogBuilder()
                .id(new GameFileId("a7e54ff1-74d9-4bb1-b7b4-7d4528f4c16e"))
                .gameId(ExistingGames.GAME_3.getId())
                .fileSource(TestFileSource.minimalGogBuilder()
                        .originalGameTitle("Original Game 3 Title")
                        .fileTitle("Game 3 File")
                        .originalFileName("game_3.exe")
                        .build())
                .build();

        private static final GameFileJpaEntityMapper MAPPER = Mappers.getMapper(GameFileJpaEntityMapper.class);

        public static List<GameFile> getAll() {
            return List.of(
                    GOG_GAME_FILE_1_FOR_GAME_1,
                    GOG_GAME_FILE_2_FOR_GAME_1,
                    GOG_GAME_FILE_1_FOR_GAME_2,
                    GOG_GAME_FILE_1_FOR_GAME_3
            );
        }
    }

    private static class ExistingFileCopies {

        public static final FileCopy TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1_FOR_GAME_1 =
                TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("9fdad52f-b4a6-46bc-af6d-bf27f9661eae"))
                        .naturalId(new FileCopyNaturalId(
                                ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_1.getId(),
                                new BackupTargetId("f882cf23-35f9-4396-832d-bd08cd50e413")
                        ))
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final FileCopy TRACKED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1_FOR_GAME_1 =
                TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("52ecf8c9-bfb0-4345-ae02-d069a3eb5267"))
                        .naturalId(new FileCopyNaturalId(
                                ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_1.getId(),
                                new BackupTargetId("a9a91a74-8a57-4bb1-9a0c-e65bf8f449f0")
                        ))
                        .dateModified(TODAY.atStartOfDay())
                        .build();

        public static final FileCopy TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2_FOR_GAME_1 =
                TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("d6a7ae5e-25d9-4410-8f08-bdc15324dd93"))
                        .naturalId(new FileCopyNaturalId(
                                ExistingGameFiles.GOG_GAME_FILE_2_FOR_GAME_1.getId(),
                                new BackupTargetId("f882cf23-35f9-4396-832d-bd08cd50e413")
                        ))
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final FileCopy ENQUEUED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1_FOR_GAME_2 =
                TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("0cfa21fe-dc8d-4a07-a570-7207f29b9f38"))
                        .naturalId(new FileCopyNaturalId(
                                ExistingGameFiles.GOG_GAME_FILE_1_FOR_GAME_2.getId(),
                                new BackupTargetId("8327a5e0-ea30-4af2-b5b5-06a232017d97")
                        ))
                        .dateModified(TODAY.atStartOfDay())
                        .build();

        private static final FileCopyJpaEntityMapper MAPPER = Mappers.getMapper(FileCopyJpaEntityMapper.class);

        public static List<FileCopy> getAll() {
            return List.of(
                    TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1_FOR_GAME_1,
                    TRACKED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1_FOR_GAME_1,
                    TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2_FOR_GAME_1,
                    ENQUEUED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1_FOR_GAME_2
            );
        }
    }
}