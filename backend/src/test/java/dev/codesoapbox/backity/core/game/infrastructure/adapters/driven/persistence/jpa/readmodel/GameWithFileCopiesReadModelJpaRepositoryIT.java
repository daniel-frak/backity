package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetJpaEntity;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetJpaEntityMapper;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopyJpaEntity;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopyJpaEntityMapper;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.TestGameWithFileCopiesSearchFilter;
import dev.codesoapbox.backity.core.game.application.readmodel.*;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntity;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.*;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntity;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntityMapper;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.testing.jpa.TestJpaPersistenceAdapter;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControl;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class GameWithFileCopiesReadModelJpaRepositoryIT {

    private static final Pagination EVERYTHING_ON_ONE_PAGE = new Pagination(0, 99);

    private static final GameWithFileCopiesReadModel GAME_1_READ_MODEL =
            TestGameWithFileCopiesReadModel.withNoSourceFilesBuilder()
                    .withValuesFrom(SampleGames.GAME_1_CREATED_TODAY)
                    .withSourceFilesWithCopies(List.of(
                            new SourceFileWithCopiesReadModel(
                                    TestSourceFileReadModel.from(SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1),
                                    List.of(
                                            TestFileCopyReadModel.from(SampleFileCopies
                                                    .TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1_FOR_GAME_1),
                                            // There used to be a bug where pagination would be incorrect due to
                                            // counting FileCopies instead of Games.
                                            // Adding the second FileCopy here helps to verify that the bug is fixed.
                                            TestFileCopyReadModel.from(SampleFileCopies
                                                    .TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1_FOR_GAME_1)
                                    )
                            ),
                            // There used to be a bug where pagination would be incorrect due to
                            // counting SourceFiles instead of Games.
                            // Adding the second SourceFile here helps to verify that the bug is fixed.
                            new SourceFileWithCopiesReadModel(
                                    TestSourceFileReadModel.from(SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1),
                                    List.of(
                                            TestFileCopyReadModel.from(SampleFileCopies
                                                    .TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2_FOR_GAME_1)
                                    )
                            )
                    ))
                    .build();

    private static final GameWithFileCopiesReadModel GAME_2_READ_MODEL =
            TestGameWithFileCopiesReadModel.withNoSourceFilesBuilder()
                    .withValuesFrom(SampleGames.GAME_2_CREATED_YESTERDAY)
                    .withSourceFilesWithCopies(List.of(
                            new SourceFileWithCopiesReadModel(
                                    TestSourceFileReadModel.from(SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_2),
                                    List.of(
                                            TestFileCopyReadModel.from(SampleFileCopies
                                                    .ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1_FOR_GAME_2)
                                    )
                            )
                    ))
                    .build();

    private static final GameWithFileCopiesReadModel GAME_3_READ_MODEL =
            TestGameWithFileCopiesReadModel.withNoSourceFilesBuilder()
                    .withValuesFrom(SampleGames.GAME_3_CREATED_TWO_DAYS_AGO)
                    .withSourceFilesWithCopies(List.of(
                            // This game has a single SourceFile with no FileCopies.
                            // We want to make sure it's still returned from queries.
                            new SourceFileWithCopiesReadModel(
                                    TestSourceFileReadModel.from(SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_3),
                                    emptyList()
                            )
                    ))
                    .build();

    @Autowired
    private GameWithFileCopiesReadModelJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private TestJpaPersistenceAdapter<Game, GameJpaEntity> gameJpaAdapter;
    private TestJpaPersistenceAdapter<SourceFile, SourceFileJpaEntity> sourceFileJpaAdapter;
    private TestJpaPersistenceAdapter<BackupTarget, BackupTargetJpaEntity> backupTargetJpaAdapter;
    private TestJpaPersistenceAdapter<FileCopy, FileCopyJpaEntity> fileCopyJpaAdapter;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
        fileCopyJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                SampleFileCopies.MAPPER::toEntity,
                SampleFileCopies.MAPPER::toDomain,
                (em, obj) -> em.find(FileCopyJpaEntity.class, obj.getId().value())
        );
        gameJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                SampleGames.MAPPER::toEntity,
                SampleGames.MAPPER::toDomain,
                (em, obj) -> em.find(GameJpaEntity.class, obj.getId().value())
        );
        sourceFileJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                SampleSourceFiles.MAPPER::toEntity,
                SampleSourceFiles.MAPPER::toDomain,
                (em, obj) -> em.find(SourceFileJpaEntity.class, obj.getId().value())
        );
        backupTargetJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                SampleBackupTargets.MAPPER::toEntity,
                SampleBackupTargets.MAPPER::toDomain,
                (em, obj) -> em.find(BackupTargetJpaEntity.class, obj.getId().value())
        );
        persistSampleData();
    }

    private void persistSampleData() {
        gameJpaAdapter.persist(SampleGames.getAll());
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
        backupTargetJpaAdapter.persist(SampleBackupTargets.getAll());
        fileCopyJpaAdapter.persist(SampleFileCopies.getAll());
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

        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        GAME_1_READ_MODEL,
                        GAME_2_READ_MODEL,
                        GAME_3_READ_MODEL
                );
    }

    @Test
    void findAllPaginatedShouldOrderByCreatedAt() {
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.empty();

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                        GAME_1_READ_MODEL,
                        GAME_2_READ_MODEL,
                        GAME_3_READ_MODEL
                );
    }

    @Test
    void findAllPaginatedShouldProperlyPaginate() {
        var pagination = new Pagination(0, 1);
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.empty();

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(pagination, filter);

        assertThat(result.content().size()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.pagination()).isEqualTo(pagination);
    }

    @Test
    void findAllPaginatedShouldFetchEntireAggregateWithoutAdditionalLazyQueries() {
        Statistics statistics = getHibernateStatistics();
        statistics.clear();

        var pagination = new Pagination(0, 1);
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(null);

        repository.findAllPaginated(pagination, filter);

        // 1 count query
        // 1 query for fetching Games
        // 1 query for fetching SourceFiles
        // 1 query for fetching FileCopies
        assertThat(statistics.getPrepareStatementCount())
                .isEqualTo(4L);
    }

    @SuppressWarnings("resource") // Spring owns SessionFactory, so we should not close it
    private Statistics getHibernateStatistics() {
        return entityManager.getEntityManager()
                .getEntityManagerFactory()
                .unwrap(SessionFactory.class)
                .getStatistics();
    }

    @Test
    void findAllPaginatedShouldTokenizeEveryWordGivenNotQuoted() {
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

        assertThat(result.content())
                .containsExactly(GAME_2_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByFileCopyStatus() {
        var filter = new GameWithFileCopiesSearchFilter(null, FileCopyStatus.ENQUEUED);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_2_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByFullGameTitleCaseInsensitive() {
        var searchQuery = "\"" + SampleGames.GAME_1_CREATED_TODAY.getTitle().value().toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfGameTitleCaseInsensitive() {
        var partOfGameTitleQuery = "\"EST GAME 1\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfGameTitleQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByFullFileTitleCaseInsensitive() {
        var searchQuery = "\"" + SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.getFileTitle().value()
                .toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfFileTitleCaseInsensitive() {
        var partOfFileTitleQuery = "\"1 (INSTALLER\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfFileTitleQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByFullOriginalFileNameCaseInsensitive() {
        var searchQuery = "\"" + SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.getOriginalFileName().value()
                .toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfOriginalFileNameCaseInsensitive() {
        var partOfOriginalFileNameQuery = "\"1_INSTALLER.EX\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfOriginalFileNameQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByFullOriginalGameTitleCaseInsensitive() {
        var searchQuery = "\"" + SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.getOriginalGameTitle().value()
                .toUpperCase() + "\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldFilterByPartOfOriginalGameTitleCaseInsensitive() {
        var partOfOriginalGameTitleSearchQuery = "\"RIGINAL GAME 1\"";
        GameWithFileCopiesSearchFilter filter =
                TestGameWithFileCopiesSearchFilter.onlySearchQuery(partOfOriginalGameTitleSearchQuery);

        Page<GameWithFileCopiesReadModel> result =
                repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content())
                .containsExactly(GAME_1_READ_MODEL);
    }

    @Test
    void findAllPaginatedShouldReturnEmptyListGivenNothingMatches() {
        var searchQuery = "\"notMatchingAnything\"";
        GameWithFileCopiesSearchFilter filter = TestGameWithFileCopiesSearchFilter.onlySearchQuery(searchQuery);

        Page<GameWithFileCopiesReadModel> result = repository.findAllPaginated(EVERYTHING_ON_ONE_PAGE, filter);

        assertThat(result.content()).isEmpty();
    }

    private static class Time {
        public static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;
        public static final LocalDate TODAY = NOW.toLocalDate();
        public static final LocalDate YESTERDAY = TODAY.minusDays(1);
        public static final LocalDate TWO_DAYS_AGO = TODAY.minusDays(2);
    }

    private static class SampleGames {

        public static final Game GAME_1_CREATED_TODAY = TestGame.anyBuilder()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle(new GameTitle("Test Game 1001"))
                .withDateCreated(Time.TODAY.atStartOfDay())
                .build();

        public static final Game GAME_2_CREATED_YESTERDAY = TestGame.anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                // [_%\] included to test escaping characters:
                .withTitle(new GameTitle("Test_Game 2 - 100% Better\\Edition"))
                .withDateCreated(Time.YESTERDAY.atStartOfDay())
                .build();

        public static final Game GAME_3_CREATED_TWO_DAYS_AGO = TestGame.anyBuilder()
                .withId(new GameId("0154e14b-b531-4748-9943-9b33ef596c2f"))
                .withTitle(new GameTitle("Test Game 3"))
                .withDateCreated(Time.TWO_DAYS_AGO.atStartOfDay())
                .build();

        private static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static List<Game> getAll() {
            return List.of(
                    GAME_1_CREATED_TODAY,
                    GAME_2_CREATED_YESTERDAY,
                    GAME_3_CREATED_TWO_DAYS_AGO
            );
        }
    }

    private static class SampleSourceFiles {

        public static final SourceFile GOG_SOURCE_FILE_1_FOR_GAME_1 = TestSourceFile.gogBuilder()
                .id(new SourceFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(SampleGames.GAME_1_CREATED_TODAY.getId())
                .originalGameTitle(new GameTitle("Original Game 1 Title"))
                .fileTitle(new FileTitle("Game 1 (Installer)"))
                .originalFileName(new FileName("game_1_installer.exe"))
                .build();

        public static final SourceFile GOG_SOURCE_FILE_2_FOR_GAME_1 = TestSourceFile.gogBuilder()
                .id(new SourceFileId("119c7d15-4d83-46d0-9fd2-511f3abae641"))
                .gameId(SampleGames.GAME_1_CREATED_TODAY.getId())
                .originalGameTitle(new GameTitle("Original Game 1 Title"))
                .fileTitle(new FileTitle("Game 1 (Patch)"))
                .originalFileName(new FileName("game_1_patch.exe"))
                .build();

        public static final SourceFile GOG_SOURCE_FILE_1_FOR_GAME_2 = TestSourceFile.gogBuilder()
                .id(new SourceFileId("533f6571-d125-4a7e-a2f2-dc066e8ce538"))
                .gameId(SampleGames.GAME_2_CREATED_YESTERDAY.getId())
                .originalGameTitle(new GameTitle("Original Game 2 Title"))
                .fileTitle(new FileTitle("Game 2 File"))
                .originalFileName(new FileName("game_2.exe"))
                .build();

        public static final SourceFile GOG_SOURCE_FILE_1_FOR_GAME_3 = TestSourceFile.gogBuilder()
                .id(new SourceFileId("a7e54ff1-74d9-4bb1-b7b4-7d4528f4c16e"))
                .gameId(SampleGames.GAME_3_CREATED_TWO_DAYS_AGO.getId())
                .originalGameTitle(new GameTitle("Original Game 3 Title"))
                .fileTitle(new FileTitle("Game 3 File"))
                .originalFileName(new FileName("game_3.exe"))
                .build();

        private static final SourceFileJpaEntityMapper MAPPER = Mappers.getMapper(SourceFileJpaEntityMapper.class);

        public static List<SourceFile> getAll() {
            return List.of(
                    GOG_SOURCE_FILE_1_FOR_GAME_1,
                    GOG_SOURCE_FILE_2_FOR_GAME_1,
                    GOG_SOURCE_FILE_1_FOR_GAME_2,
                    GOG_SOURCE_FILE_1_FOR_GAME_3
            );
        }
    }

    private static class SampleBackupTargets {

        public static final BackupTargetJpaEntityMapper MAPPER = Mappers.getMapper(BackupTargetJpaEntityMapper.class);

        public static final Supplier<BackupTarget> LOCAL_FOLDER_1 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76"))
                .build();

        public static final Supplier<BackupTarget> LOCAL_FOLDER_2 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("16744bc6-6e7e-4ef8-b009-bd77c839d914"))
                .build();

        public static final Supplier<BackupTarget> LOCAL_FOLDER_3 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("3db4150a-9b80-42f8-9979-7db22de58502"))
                .build();

        public static final Supplier<BackupTarget> LOCAL_FOLDER_4 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("fd69b069-bba1-4163-ad42-65e1574aacb3"))
                .build();

        public static List<BackupTarget> getAll() {
            return List.of(
                    LOCAL_FOLDER_1.get(),
                    LOCAL_FOLDER_2.get(),
                    LOCAL_FOLDER_3.get(),
                    LOCAL_FOLDER_4.get()
            );
        }
    }

    private static class SampleFileCopies {

        public static final FileCopy TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1_FOR_GAME_1 =
                TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("9fdad52f-b4a6-46bc-af6d-bf27f9661eae"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.getId(),
                                SampleBackupTargets.LOCAL_FOLDER_1.get().getId()
                        ))
                        .dateModified(Time.YESTERDAY.atStartOfDay())
                        .build();

        public static final FileCopy TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1_FOR_GAME_1 =
                TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("52ecf8c9-bfb0-4345-ae02-d069a3eb5267"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.getId(),
                                SampleBackupTargets.LOCAL_FOLDER_2.get().getId()
                        ))
                        .dateModified(Time.TODAY.atStartOfDay())
                        .build();

        public static final FileCopy TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2_FOR_GAME_1 =
                TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("d6a7ae5e-25d9-4410-8f08-bdc15324dd93"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.getId(),
                                SampleBackupTargets.LOCAL_FOLDER_3.get().getId()
                        ))
                        .dateModified(Time.YESTERDAY.atStartOfDay())
                        .build();

        public static final FileCopy ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1_FOR_GAME_2 =
                TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("0cfa21fe-dc8d-4a07-a570-7207f29b9f38"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_2.getId(),
                                SampleBackupTargets.LOCAL_FOLDER_4.get().getId()
                        ))
                        .dateModified(Time.TODAY.atStartOfDay())
                        .build();

        private static final FileCopyJpaEntityMapper MAPPER = Mappers.getMapper(FileCopyJpaEntityMapper.class);

        public static List<FileCopy> getAll() {
            return List.of(
                    TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1_FOR_GAME_1,
                    TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1_FOR_GAME_1,
                    TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2_FOR_GAME_1,
                    ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1_FOR_GAME_2
            );
        }
    }
}