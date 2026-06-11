package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetJpaEntity;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetJpaEntityMapper;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntity;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntity;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntityMapper;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import dev.codesoapbox.backity.testing.jpa.TestJpaPersistenceAdapter;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControl;
import dev.codesoapbox.backity.testing.time.FakeClock;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

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

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class FileCopyJpaRepositoryIT {

    @Autowired
    protected FileCopyJpaRepository repository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected FileCopyJpaEntityMapper entityMapper;

    @Autowired
    protected DomainEventPublisher domainEventPublisher;

    @Autowired
    protected FakeClock clock;

    private TestJpaPersistenceAdapter<FileCopy, FileCopyJpaEntity> fileCopyJpaAdapter;
    private TestJpaPersistenceAdapter<Game, GameJpaEntity> gameJpaAdapter;
    private TestJpaPersistenceAdapter<SourceFile, SourceFileJpaEntity> sourceFileJpaAdapter;
    private TestJpaPersistenceAdapter<BackupTarget, BackupTargetJpaEntity> backupTargetJpaAdapter;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
        fileCopyJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
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
    }

    @Test
    void saveShouldPersistNew() {
        persistSampleDependencies();
        FileCopy fileCopy = TestFileCopy.trackedBuilder()
                .naturalId(new FileCopyNaturalId(
                        SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get().getId(),
                        SampleBackupTargets.LOCAL_FOLDER_1.get().getId()
                ))
                .build();

        repository.save(fileCopy);
        entityManager.flush();

        FileCopy persistedAggregate = fileCopyJpaAdapter.getPersistedDomainObject(fileCopy);
        assertThat(persistedAggregate)
                .usingRecursiveComparison()
                .isEqualTo(fileCopy);
    }

    private void persistSampleDependencies() {
        gameJpaAdapter.persist(SampleGames.getAll());
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
        backupTargetJpaAdapter.persist(SampleBackupTargets.getAll());
    }

    @Test
    void saveShouldModifyExisting() {
        persistSampleData();
        FileCopy fileCopy = SampleFileCopies.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get();
        fileCopy.toInProgress(new FilePath("someFilePath"));

        repository.save(fileCopy);
        entityManager.flush();

        FileCopy persistedAggregate = fileCopyJpaAdapter.getPersistedDomainObject(fileCopy);
        assertThat(persistedAggregate)
                .usingRecursiveComparison()
                .isEqualTo(fileCopy);
    }

    private void persistSampleData() {
        persistSampleDependencies();
        fileCopyJpaAdapter.persist(SampleFileCopies.getAll());
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDatesGivenNew(EntityAuditControl entityAuditControl) {
        persistSampleDependencies();
        entityAuditControl.enable();
        FileCopy fileCopy = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();

        repository.save(fileCopy);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now(clock);
        FileCopy persistedAggregate = fileCopyJpaAdapter.getPersistedDomainObject(fileCopy);
        assertThat(persistedAggregate.getDateCreated())
                .isNotEqualTo(fileCopy.getDateCreated())
                .isEqualTo(now);
        assertThat(persistedAggregate.getDateModified())
                .isNotEqualTo(fileCopy.getDateModified())
                .isEqualTo(now);
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDatesGivenExisting(EntityAuditControl entityAuditControl) {
        persistSampleData();
        entityAuditControl.enable();
        FileCopy fileCopy = SampleFileCopies.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get();
        fileCopy.toInProgress(new FilePath("someFilePath"));

        repository.save(fileCopy);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now(clock);
        FileCopy persistedAggregate = fileCopyJpaAdapter.getPersistedDomainObject(fileCopy);
        assertThat(persistedAggregate.getDateCreated())
                .isEqualTo(fileCopy.getDateCreated())
                .isNotEqualTo(now);
        assertThat(persistedAggregate.getDateModified())
                .isNotEqualTo(fileCopy.getDateModified())
                .isEqualTo(now);
    }

    @Test
    void saveShouldPublishEvents() {
        persistSampleData();
        FileCopy fileCopy = TestFileCopy.enqueued();
        var aFilePath = new FilePath("someFilePath");
        fileCopy.toInProgress(aFilePath);
        repository.save(fileCopy);

        verify(domainEventPublisher).publish(any(FileBackupStartedEvent.class));
    }

    @Test
    void saveShouldClearEvents() {
        persistSampleData();
        FileCopy fileCopy = TestFileCopy.enqueued();
        var aFilePath = new FilePath("someFilePath");
        fileCopy.toInProgress(aFilePath);
        repository.save(fileCopy);

        assertThat(fileCopy.getDomainEvents()).isEmpty();
    }

    @Test
    void saveShouldThrowGivenNaturalIdIsNotUnique() {
        persistSampleDependencies();
        FileCopy fileCopy1 = TestFileCopy.trackedBuilder()
                .id(new FileCopyId("7bf408f7-9b6e-4ee7-a2f2-27a454a4f5ba"))
                .build();
        FileCopy fileCopy2 = TestFileCopy.trackedBuilder()
                .id(new FileCopyId("09366863-b707-4e5c-8315-ed7e7c56d0a9"))
                .naturalId(fileCopy1.getNaturalId())
                .build();
        fileCopyJpaAdapter.persist(fileCopy1);

        repository.save(fileCopy2);

        assertThatThrownBy(() -> entityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getByIdShouldReturnAggregateGivenItExists() {
        persistSampleData();
        FileCopy expectedResult = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();
        FileCopy result = repository.getById(expectedResult.getId());

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        persistSampleData();
        var nonExistentId = new FileCopyId("247900d6-8829-47cd-a068-4f35f37d8eb8");

        assertThatThrownBy(() -> repository.getById(nonExistentId))
                .isInstanceOf(FileCopyNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    @Test
    void findByNaturalIdOrCreateShouldGetGivenExists() {
        persistSampleData();
        FileCopy expectedResult = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();

        FileCopy result = repository.getByNaturalIdOrCreate(expectedResult.getNaturalId(), () -> null);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void findByNaturalIdOrCreateShouldGetGivenNotExists() {
        persistSampleDependencies();
        FileCopy expectedResult = TestFileCopy.trackedBuilder()
                .id(new FileCopyId("7bf408f7-9b6e-4ee7-a2f2-27a454a4f5ba"))
                .naturalId(new FileCopyNaturalId(
                        SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                        SampleBackupTargets.LOCAL_FOLDER_1.get().getId()
                ))
                .build();

        FileCopy result = repository.getByNaturalIdOrCreate(expectedResult.getNaturalId(), () -> expectedResult);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void findByNaturalIdOrCreateShouldGetGivenCreatedInParallel() {
        persistSampleData();
        FileCopySpringRepository mockSpringRepository = mock(FileCopySpringRepository.class);
        repository = new FileCopyJpaRepository(mockSpringRepository, entityMapper,
                mock(SpringPageMapper.class), mock(SpringPageableMapper.class), domainEventPublisher);
        FileCopy expectedExisting = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();
        FileCopyNaturalId naturalId = expectedExisting.getNaturalId();
        FileCopy newFileCopy = TestFileCopy.trackedBuilder()
                .naturalId(naturalId)
                .build();
        aggregateIsCreatedConcurrently(mockSpringRepository, naturalId, expectedExisting);

        FileCopy result = repository.getByNaturalIdOrCreate(naturalId, () -> newFileCopy);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedExisting);
    }

    private void aggregateIsCreatedConcurrently(FileCopySpringRepository mockSpringRepository, FileCopyNaturalId naturalId, FileCopy expectedExisting) {
        when(mockSpringRepository.findByNaturalIdSourceFileIdAndNaturalIdBackupTargetId(
                naturalId.sourceFileId().value(), naturalId.backupTargetId().value()))
                // Nothing in DB during initial lookup:
                .thenReturn(Optional.empty());
        when(mockSpringRepository.save(any()))
                // Someone saved before we did
                .thenThrow(new DataIntegrityViolationException("unique constraint violation"));
        when(mockSpringRepository.getByNaturalIdSourceFileIdAndNaturalIdBackupTargetId(
                naturalId.sourceFileId().value(), naturalId.backupTargetId().value()))
                // Someone saved before second lookup:
                .thenReturn(entityMapper.toEntity(expectedExisting));
    }

    @Test
    void findOldestEnqueuedShouldReturnOldestEnqueued() {
        persistSampleData();
        Optional<FileCopy> result = repository.findOldestEnqueued();

        FileCopy expectedResult = SampleFileCopies.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get();
        assertThat(result).isPresent();
        assertThat(result.get())
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void findAllInProgressOrEnqueuedShouldReturnInProgressAndEnqueued() {
        persistSampleData();
        Pagination pagination = everythingOnOnePage();
        List<FileCopy> expectedContent = List.of(
                SampleFileCopies.IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get()
        );

        Page<FileCopy> result = repository.findAllInProgressOrEnqueued(pagination);

        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedContent);
    }

    private Pagination everythingOnOnePage() {
        return new Pagination(0, 999);
    }

    @Test
    void findAllInProgressOrEnqueuedShouldProperlyPaginate() {
        persistSampleData();
        Pagination pagination = new Pagination(0, 1);

        Page<FileCopy> result = repository.findAllInProgressOrEnqueued(pagination);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.totalElements()).isEqualTo(3);
        assertThat(result.pagination()).isEqualTo(pagination);
    }

    @Test
    void findAllInProgressOrEnqueuedShouldSortByStatusAscThenDateModifiedAsc() {
        persistSampleData();
        Pagination pagination = everythingOnOnePage();
        var expectedContent = List.of(
                SampleFileCopies.IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get()
        );

        Page<FileCopy> result = repository.findAllInProgressOrEnqueued(pagination);

        assertThat(result.content())
                .containsExactlyElementsOf(expectedContent);
    }

    @Test
    void findAllInProgressShouldReturnAllInProgress() {
        persistSampleData();
        List<FileCopy> expectedResult = List.of(
                SampleFileCopies.IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get()
        );

        List<FileCopy> result = repository.findAllInProgress();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void findAllBySourceFileIdShouldReturnAllMatching() {
        persistSampleData();
        List<FileCopy> result =
                repository.findAllBySourceFileId(SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get().getId());

        List<FileCopy> expectedResult = List.of(
                SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get(),
                SampleFileCopies.TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get()
        );
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedResult);
    }

    @Test
    void existByBackupTargetIdAndStatusNotInShouldReturnTrueGivenFileCopyExists() {
        persistSampleData();
        BackupTargetId existingBackupTargetId =
                SampleFileCopies.TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get().getNaturalId().backupTargetId();

        boolean result = repository.existByBackupTargetIdAndStatusNotIn(
                existingBackupTargetId, List.of(FileCopyStatus.IN_PROGRESS));

        assertThat(result).isTrue();
    }

    @Test
    void existByBackupTargetIdAndStatusNotInShouldReturnFalseGivenFileCopyNotFoundWithCorrectStatusNot() {
        persistSampleData();
        BackupTargetId existingBackupTargetId =
                SampleFileCopies.TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get().getNaturalId().backupTargetId();

        boolean result = repository.existByBackupTargetIdAndStatusNotIn(
                existingBackupTargetId, List.of(FileCopyStatus.values()));

        assertThat(result).isFalse();
    }

    @Test
    void existByBackupTargetIdAndStatusNotInShouldReturnFalseGivenFileCopyNotFoundWithCorrectIdNot() {
        persistSampleData();
        BackupTargetId existingBackupTargetId =
                SampleFileCopies.TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get().getNaturalId()
                        .backupTargetId();

        boolean result =
                repository.existByBackupTargetIdAndStatusNotIn(existingBackupTargetId, List.of(FileCopyStatus.TRACKED));

        assertThat(result).isFalse();
    }

    @Test
    void getUniqueBackupTargetIdsByStatusNotInShouldReturnIds() {
        persistSampleData();
        List<BackupTargetId> result =
                repository.getUniqueBackupTargetIdsByStatusNotIn(
                        List.of(FileCopyStatus.TRACKED, FileCopyStatus.FAILED));

        assertThat(result).containsOnly(
                SampleFileCopies.IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get()
                        .getNaturalId().backupTargetId(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get()
                        .getNaturalId().backupTargetId(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get()
                        .getNaturalId().backupTargetId(),
                SampleFileCopies.STORED_UNVERIFIED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get()
                        .getNaturalId().backupTargetId(),
                SampleFileCopies.STORED_VERIFIED_FILE_COPY_FROM_BEFORE_YESTERDAY_FOR_SOURCE_FILE_2.get()
                        .getNaturalId().backupTargetId()
        );
    }

    @Test
    void deleteTrackedWithBackupTargetIdShouldDeleteFileCopies() {
        persistSampleData();
        FileCopy fileCopy = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();
        FileCopy unaffectedFileCopy = SampleFileCopies.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get();

        repository.deleteByBackupTargetIdAndStatusIn(
                fileCopy.getNaturalId().backupTargetId(), List.of(fileCopy.getStatus()));

        assertThat(fileCopyJpaAdapter.exists(fileCopy)).isFalse();
        assertThat(fileCopyJpaAdapter.exists(unaffectedFileCopy)).isTrue();
    }

    @Test
    void deleteTrackedWithBackupTargetIdShouldNotDeleteFileCopiesWithoutMatchingStatus() {
        persistSampleData();
        FileCopy fileCopy = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();

        repository.deleteByBackupTargetIdAndStatusIn(
                fileCopy.getNaturalId().backupTargetId(), List.of(FileCopyStatus.FAILED));

        assertThat(fileCopyJpaAdapter.exists(fileCopy)).isTrue();
    }

    private static class Time {

        public static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;
        public static final LocalDate TODAY = NOW.toLocalDate();
        public static final LocalDate YESTERDAY = TODAY.minusDays(1);
        public static final LocalDate BEFORE_YESTERDAY = TODAY.minusDays(2);
    }

    private static class SampleGames {

        public static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle(new GameTitle("Game 1"))
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_1);
        }
    }

    private static class SampleSourceFiles {

        public static final SourceFileJpaEntityMapper MAPPER = Mappers.getMapper(SourceFileJpaEntityMapper.class);

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_1_FOR_GAME_1 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(SampleGames.GAME_1.getId())
                .build();

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_2_FOR_GAME_1 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("a6adc122-df20-4e2c-a975-7d4af7104704"))
                .gameId(SampleGames.GAME_1.getId())
                .build();

        public static List<SourceFile> getAll() {
            return List.of(
                    GOG_SOURCE_FILE_1_FOR_GAME_1.get(), GOG_SOURCE_FILE_2_FOR_GAME_1.get()
            );
        }
    }

    private static class SampleBackupTargets {

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

        public static final Supplier<BackupTarget> LOCAL_FOLDER_5 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("c2c701a4-db6d-4ee8-ba54-0bd9b3064304"))
                .build();

        public static final Supplier<BackupTarget> LOCAL_FOLDER_6 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("3c7e9c77-35fe-413c-867b-1429efc5baf6"))
                .build();

        public static final Supplier<BackupTarget> LOCAL_FOLDER_7 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("cd4a7b14-9a8b-4af0-9529-cfcb43372154"))
                .build();

        public static final Supplier<BackupTarget> LOCAL_FOLDER_8 = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("03468b45-7152-4026-b416-6a2602bf0c1c"))
                .build();

        private static final BackupTargetJpaEntityMapper MAPPER = Mappers.getMapper(BackupTargetJpaEntityMapper.class);

        public static List<BackupTarget> getAll() {
            return List.of(
                    LOCAL_FOLDER_1.get(),
                    LOCAL_FOLDER_2.get(),
                    LOCAL_FOLDER_3.get(),
                    LOCAL_FOLDER_4.get(),
                    LOCAL_FOLDER_5.get(),
                    LOCAL_FOLDER_6.get(),
                    LOCAL_FOLDER_7.get(),
                    LOCAL_FOLDER_8.get()
            );
        }
    }

    private static class SampleFileCopies {

        public static final Supplier<FileCopy> TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1 =
                () -> TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("9fdad52f-b4a6-46bc-af6d-bf27f9661eae"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_1.get().getId()
                        ))
                        .filePath(new FilePath("filePath1"))
                        .dateModified(Time.YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1 =
                () -> TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("773a79ae-6cfa-4264-b76e-7accffdb9f34"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_2.get().getId()
                        ))
                        .filePath(new FilePath("filePath2"))
                        .dateModified(Time.TODAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.inProgressBuilder()
                        .id(new FileCopyId("279baabb-f301-441b-a312-edf9babc84b2"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_3.get().getId()
                        ))
                        .filePath(new FilePath("filePath3"))
                        .dateModified(Time.YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("70fc9396-c7e1-4fe3-8718-7c79bac1cbb2"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_4.get().getId()
                        ))
                        .filePath(new FilePath("filePath4"))
                        .dateModified(Time.YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("4557eaf0-a603-4003-8a35-5ab588d24f88"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_5.get().getId()
                        ))
                        .filePath(new FilePath("filePath5"))
                        .dateModified(Time.TODAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> STORED_UNVERIFIED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.storedIntegrityUnknownBuilder()
                        .id(new FileCopyId("e3e5636d-bb13-4506-87eb-c22d238defce"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_6.get().getId()
                        ))
                        .filePath(new FilePath("filePath6"))
                        .dateModified(Time.YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> STORED_VERIFIED_FILE_COPY_FROM_BEFORE_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.storedIntegrityVerifiedBuilder()
                        .id(new FileCopyId("3ff35991-86a4-4225-8d80-d157bd60193c"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_7.get().getId()
                        ))
                        .filePath(new FilePath("filePath7"))
                        .dateModified(Time.BEFORE_YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> FAILED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.failedWithoutFilePathBuilder()
                        .id(new FileCopyId("5bac0242-14bc-435f-a187-387026cc8245"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_8.get().getId()
                        ))
                        .filePath(new FilePath("filePath8"))
                        .dateModified(Time.TODAY.atStartOfDay())
                        .build();

        public static List<FileCopy> getAll() {
            return List.of(
                    TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get(),
                    TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get(),
                    IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                    ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                    ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get(),
                    STORED_UNVERIFIED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                    STORED_VERIFIED_FILE_COPY_FROM_BEFORE_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                    FAILED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get()
            );
        }
    }
}