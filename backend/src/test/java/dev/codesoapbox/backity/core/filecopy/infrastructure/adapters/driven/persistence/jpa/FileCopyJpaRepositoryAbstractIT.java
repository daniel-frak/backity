package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa.GameFileJpaEntityMapper;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.codesoapbox.backity.core.game.domain.TestGame.anyBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
abstract class FileCopyJpaRepositoryAbstractIT {

    private static final LocalDateTime NOW = FakeTimeBeanConfig.FIXED_DATE_TIME;
    private static final LocalDate TODAY = NOW.toLocalDate();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate BEFORE_YESTERDAY = TODAY.minusDays(2);

    @Autowired
    protected FileCopyJpaRepository repository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected FileCopyJpaEntityMapper entityMapper;

    @Autowired
    protected DomainEventPublisher domainEventPublisher;

    @BeforeEach
    void setUp() {
        persistExistingDependencies();
    }

    private void persistExistingDependencies() {
        for (Game game : EXISTING_GAMES.getAll()) {
            entityManager.persist(EXISTING_GAMES.MAPPER.toEntity(game));
        }
        for (GameFile gameFile : EXISTING_GAME_FILES.getAll()) {
            entityManager.persist(EXISTING_GAME_FILES.MAPPER.toEntity(gameFile));
        }
        for (FileCopy fileCopy : EXISTING_FILE_COPIES.getAll()) {
            entityManager.persist(entityMapper.toEntity(fileCopy));
            updateDateModified(fileCopy);
        }
    }

    /*
    DateModified gets overwritten by the database on every INSERT/UPDATE, so we need to update it manually.
     */
    private void updateDateModified(FileCopy fileCopy) {
        entityManager.getEntityManager()
                .createQuery("UPDATE FileCopy f SET f.dateModified = :date WHERE f.id = :id")
                .setParameter("date", fileCopy.getDateModified())
                .setParameter("id", fileCopy.getId().value())
                .executeUpdate();
    }

    @Test
    void saveShouldPersistNew() {
        FileCopy fileCopy = TestFileCopy.trackedBuilder()
                .naturalId(new FileCopyNaturalId(
                        EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get().getId(),
                        new BackupTargetId("290035b5-388c-4705-b89c-73950eb61b75")
                ))
                .build();

        FileCopy result = repository.save(fileCopy);
        entityManager.flush();

        assertSame(result, fileCopy);
        assertWasPersisted(fileCopy);
    }

    private void assertWasPersisted(FileCopy fileCopy) {
        FileCopyJpaEntity persistedEntity = entityManager.find(FileCopyJpaEntity.class, fileCopy.getId().value());
        FileCopy persistedFileCopy = entityMapper.toDomain(persistedEntity);
        assertSame(persistedFileCopy, fileCopy);
    }

    private void assertSame(FileCopy actual, FileCopy expected) {
        assertThat(actual)
                .satisfies(it -> {
                    assertThat(it.getDateCreated()).isNotNull();
                    assertThat(it.getDateModified()).isNotNull();
                })
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expected);
    }

    @Test
    void saveShouldModifyExisting() {
        FileCopy fileCopy = EXISTING_FILE_COPIES.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_2.get();
        fileCopy.toInProgress("someFilePath");

        FileCopy result = repository.save(fileCopy);
        entityManager.flush();

        assertSame(result, fileCopy);
        assertWasPersisted(fileCopy);
    }

    @Test
    void saveShouldPublishEventsAfterCommitting() {
        FileCopy fileCopy = TestFileCopy.enqueued();
        fileCopy.toInProgress("someFilePath");
        repository.save(fileCopy);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

        verify(domainEventPublisher).publish(any(FileBackupStartedEvent.class));
    }

    @Test
    void saveShouldClearEvents() {
        FileCopy fileCopy = TestFileCopy.enqueued();
        fileCopy.toInProgress("someFilePath");
        repository.save(fileCopy);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

        assertThat(fileCopy.getDomainEvents()).isEmpty();
    }

    @Test
    void saveShouldThrowGivenNaturalIdIsNotUnique() {
        FileCopy fileCopy1 = TestFileCopy.trackedBuilder()
                .id(new FileCopyId("7bf408f7-9b6e-4ee7-a2f2-27a454a4f5ba"))
                .build();
        FileCopy fileCopy2 = TestFileCopy.trackedBuilder()
                .id(new FileCopyId("09366863-b707-4e5c-8315-ed7e7c56d0a9"))
                .naturalId(fileCopy1.getNaturalId())
                .build();
        repository.save(fileCopy1);
        repository.save(fileCopy2);

        assertThatThrownBy(() -> entityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldGetById() {
        FileCopy result = repository.getById(
                EXISTING_FILE_COPIES.DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1.get().getId());

        assertSame(result, EXISTING_FILE_COPIES.DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1.get());
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        var nonExistentId = new FileCopyId("247900d6-8829-47cd-a068-4f35f37d8eb8");

        assertThatThrownBy(() -> repository.getById(nonExistentId))
                .isInstanceOf(FileCopyNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    @Test
    void findByNaturalIdOrCreateShouldFindGivenExists() {
        FileCopy expectedFileCopy = EXISTING_FILE_COPIES.DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1.get();
        FileCopy result = repository.findByNaturalIdOrCreate(expectedFileCopy.getNaturalId(), () -> null);

        assertSame(result, expectedFileCopy);
    }

    @Test
    void findByNaturalIdOrCreateShouldFindGivenNotExists() {
        FileCopy expectedFileCopy = TestFileCopy.trackedBuilder()
                .id(new FileCopyId("7bf408f7-9b6e-4ee7-a2f2-27a454a4f5ba"))
                .build();
        FileCopy result = repository.findByNaturalIdOrCreate(expectedFileCopy.getNaturalId(), () -> expectedFileCopy);

        assertSame(result, expectedFileCopy);
    }

    @Test
    void findByNaturalIdOrCreateShouldFindGivenCreatedInParallel() {
        FileCopySpringRepository mockSpringRepository = mock(FileCopySpringRepository.class);
        repository = new FileCopyJpaRepository(mockSpringRepository, entityMapper,
                mock(PageEntityMapper.class), mock(PaginationEntityMapper.class), domainEventPublisher);
        FileCopy expectedExisting = EXISTING_FILE_COPIES.DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1.get();
        FileCopyNaturalId naturalId = expectedExisting.getNaturalId();
        FileCopy newFileCopy = TestFileCopy.trackedBuilder()
                .naturalId(naturalId)
                .build();
        when(mockSpringRepository.findByNaturalIdGameFileIdAndNaturalIdBackupTargetId(
                naturalId.gameFileId().value(), naturalId.backupTargetId().value()))
                // Nothing in DB during initial lookup:
                .thenReturn(Optional.empty());
        when(mockSpringRepository.save(any()))
                // Someone saved before we did
                .thenThrow(new DataIntegrityViolationException("unique constraint violation"));
        when(mockSpringRepository.getByNaturalIdGameFileIdAndNaturalIdBackupTargetId(
                naturalId.gameFileId().value(), naturalId.backupTargetId().value()))
                // Someone saved before second lookup:
                .thenReturn(entityMapper.toEntity(expectedExisting));

        FileCopy result = repository.findByNaturalIdOrCreate(naturalId, () -> newFileCopy);

        assertSame(result, expectedExisting);
    }

    @Test
    void shouldFindOneInProgress() {
        Optional<FileCopy> result = repository.findOneInProgress();

        assertThat(result).isPresent();
        assertSame(result.get(), EXISTING_FILE_COPIES.IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2.get());
    }

    @Test
    void shouldFindOldestEnqueued() {
        Optional<FileCopy> result = repository.findOldestEnqueued();

        FileCopy expectedResult = EXISTING_FILE_COPIES.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2.get();
        assertThat(result).isPresent();
        assertSame(result.get(), expectedResult);
    }

    @Test
    void shouldFindAllEnqueuedInOrderOfDateModifiedAscending() {
        var pagination = new Pagination(0, 2);

        Page<FileCopy> result = repository.findAllEnqueued(pagination);

        List<FileCopy> expectedItems = List.of(
                EXISTING_FILE_COPIES.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2.get(),
                EXISTING_FILE_COPIES.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_2.get()
        );
        int totalPages = 1;
        int totalElements = 2;
        assertContainsInOrder(result, pagination, totalPages, totalElements, expectedItems);
    }

    private void assertContainsInOrder(Page<FileCopy> result, Pagination pagination, int totalPages, int totalElements,
                                       List<FileCopy> items) {
        Page<FileCopy> expectedResult = pageFrom(pagination, totalPages, totalElements, items);
        assertSame(result, expectedResult);
    }

    private Page<FileCopy> pageFrom(Pagination pagination, int totalPages, int totalElements,
                                    List<FileCopy> items) {
        return new Page<>(
                items,
                pagination.pageSize(),
                totalPages,
                totalElements,
                pagination.pageSize(),
                pagination.pageNumber()
        );
    }

    private void assertSame(Page<FileCopy> result, Page<FileCopy> expectedResult) {
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("content.dateCreated", "content.dateModified")
                .isEqualTo(expectedResult);
        assertThat(result.content()).containsExactlyElementsOf(expectedResult.content());
    }

    @Test
    void shouldFindAllProcessedInOrderOfDateModifiedAscending() {
        var pagination = new Pagination(0, 3);

        Page<FileCopy> result = repository.findAllProcessed(pagination);

        List<FileCopy> expectedItems = List.of(
                EXISTING_FILE_COPIES.STORED_VERIFIED_FILE_COPY_FROM_BEFORE_YESTERDAY_FOR_GAME_FILE_2.get(),
                EXISTING_FILE_COPIES.STORED_UNVERIFIED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2.get(),
                EXISTING_FILE_COPIES.FAILED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_2.get()
        );
        int totalPages = 1;
        int totalElements = 3;
        assertContainsInOrder(result, pagination, totalPages, totalElements, expectedItems);
    }

    @Test
    void shouldFindAllByGameFileId() {
        List<FileCopy> result =
                repository.findAllByGameFileId(EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get().getId());

        List<FileCopy> expectedResult = List.of(
                EXISTING_FILE_COPIES.DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1.get(),
                EXISTING_FILE_COPIES.DISCOVERED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1.get()
        );
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("dateCreated", "dateModified")
                .containsExactlyElementsOf(expectedResult);
    }

    private static class EXISTING_GAMES {

        public static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle("Game 1")
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_1);
        }
    }

    private static class EXISTING_GAME_FILES {

        public static final GameFileJpaEntityMapper MAPPER = Mappers.getMapper(GameFileJpaEntityMapper.class);

        public static final Supplier<GameFile> GOG_GAME_FILE_1_FOR_GAME_1 = () -> TestGameFile.gogBuilder()
                .id(new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<GameFile> GOG_GAME_FILE_2_FOR_GAME_1 = () -> TestGameFile.gogBuilder()
                .id(new GameFileId("a6adc122-df20-4e2c-a975-7d4af7104704"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static List<GameFile> getAll() {
            return List.of(
                    GOG_GAME_FILE_1_FOR_GAME_1.get(), GOG_GAME_FILE_2_FOR_GAME_1.get()
            );
        }
    }

    private static class EXISTING_FILE_COPIES {

        public static final Supplier<FileCopy> DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1 =
                () -> TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("9fdad52f-b4a6-46bc-af6d-bf27f9661eae"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get().getId(),
                                new BackupTargetId("f882cf23-35f9-4396-832d-bd08cd50e413")
                        ))
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> DISCOVERED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1 =
                () -> TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("773a79ae-6cfa-4264-b76e-7accffdb9f34"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get().getId(),
                                new BackupTargetId("ab150d94-c56c-4bd5-9a73-4c0427b48ede")
                        ))
                        .dateModified(TODAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2 =
                () -> TestFileCopy.inProgressBuilder()
                        .id(new FileCopyId("279baabb-f301-441b-a312-edf9babc84b2"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get().getId(),
                                new BackupTargetId("17b72fc0-4f2b-41da-b4d4-9b33be11f990")
                        ))
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2 =
                () -> TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("70fc9396-c7e1-4fe3-8718-7c79bac1cbb2"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get().getId(),
                                new BackupTargetId("75653edd-f557-4262-a378-ae3877143fc6")
                        ))
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> ENQUEUED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_2 =
                () -> TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("4557eaf0-a603-4003-8a35-5ab588d24f88"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get().getId(),
                                new BackupTargetId("a7380842-7f49-4499-96b7-93f75de60c8b")
                        ))
                        .dateModified(TODAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> STORED_UNVERIFIED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2 =
                () -> TestFileCopy.storedIntegrityUnknownBuilder()
                        .id(new FileCopyId("e3e5636d-bb13-4506-87eb-c22d238defce"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get().getId(),
                                new BackupTargetId("9a22c5d8-7540-4bd3-8cd3-e56c97fe6550")
                        ))
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> STORED_VERIFIED_FILE_COPY_FROM_BEFORE_YESTERDAY_FOR_GAME_FILE_2 =
                () -> TestFileCopy.storedIntegrityVerifiedBuilder()
                        .id(new FileCopyId("3ff35991-86a4-4225-8d80-d157bd60193c"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get().getId(),
                                new BackupTargetId("9f8b7374-0041-4cdf-9003-c3c3b08f32ac")
                        ))
                        .dateModified(BEFORE_YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> FAILED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_2 =
                () -> TestFileCopy.failedWithoutFilePathBuilder()
                        .id(new FileCopyId("5bac0242-14bc-435f-a187-387026cc8245"))
                        .naturalId(new FileCopyNaturalId(
                                EXISTING_GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get().getId(),
                                new BackupTargetId("ac548b50-104c-41a5-97b0-8857eb743a71")
                        ))
                        .dateModified(TODAY.atStartOfDay())
                        .build();

        public static List<FileCopy> getAll() {
            return List.of(
                    DISCOVERED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_1.get(),
                    DISCOVERED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_1.get(),
                    IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2.get(),
                    ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2.get(),
                    ENQUEUED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_2.get(),
                    STORED_UNVERIFIED_FILE_COPY_FROM_YESTERDAY_FOR_GAME_FILE_2.get(),
                    STORED_VERIFIED_FILE_COPY_FROM_BEFORE_YESTERDAY_FOR_GAME_FILE_2.get(),
                    FAILED_FILE_COPY_FROM_TODAY_FOR_GAME_FILE_2.get()
            );
        }
    }
}