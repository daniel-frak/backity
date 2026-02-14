package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetJpaEntityMapper;
import dev.codesoapbox.backity.core.filecopy.domain.*;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntityMapper;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
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

    private static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;
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

    private void persistSampleData() {
        persistSampleDependencies();
        persistFileCopies(SampleFileCopies.getAll());
    }

    private void persistSampleDependencies() {
        persistGames(SampleGames.getAll());
        persistSourceFiles(SampleSourceFiles.getAll());
        persistBackupTargets(SampleBackupTargets.getAll());
    }

    private void persistGames(List<Game> games) {
        for (Game game : games) {
            entityManager.persist(SampleGames.MAPPER.toEntity(game));
        }
    }

    private void persistSourceFiles(List<SourceFile> sourceFiles) {
        for (SourceFile sourceFile : sourceFiles) {
            entityManager.persist(SampleSourceFiles.MAPPER.toEntity(sourceFile));
        }
    }

    private void persistBackupTargets(List<BackupTarget> backupTargets) {
        for (BackupTarget backupTarget : backupTargets) {
            entityManager.persist(SampleBackupTargets.MAPPER.toEntity(backupTarget));
        }
    }

    private void persistFileCopies(List<FileCopy> fileCopies) {
        for (FileCopy fileCopy : fileCopies) {
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
        persistSampleDependencies();
        FileCopy fileCopy = TestFileCopy.trackedBuilder()
                .naturalId(new FileCopyNaturalId(
                        SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get().getId(),
                        SampleBackupTargets.LOCAL_FOLDER_1.get().getId()
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
        persistSampleData();
        FileCopy fileCopy = SampleFileCopies.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get();
        fileCopy.toInProgress("someFilePath");

        FileCopy result = repository.save(fileCopy);
        entityManager.flush();

        assertSame(result, fileCopy);
        assertWasPersisted(fileCopy);
    }

    @Test
    void saveShouldPublishEventsAfterCommitting() {
        persistSampleData();
        FileCopy fileCopy = TestFileCopy.enqueued();
        fileCopy.toInProgress("someFilePath");
        repository.save(fileCopy);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

        verify(domainEventPublisher).publish(any(FileBackupStartedEvent.class));
    }

    @Test
    void saveShouldClearEvents() {
        persistSampleData();
        FileCopy fileCopy = TestFileCopy.enqueued();
        fileCopy.toInProgress("someFilePath");
        repository.save(fileCopy);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

        assertThat(fileCopy.getDomainEvents()).isEmpty();
    }

    @Test
    void saveShouldThrowGivenNaturalIdIsNotUnique() {
        persistSampleData();
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
        persistSampleData();
        FileCopy result = repository.getById(
                SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get().getId());

        assertSame(result, SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get());
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
    void findByNaturalIdOrCreateShouldFindGivenExists() {
        persistSampleData();
        FileCopy expectedFileCopy = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();
        FileCopy result = repository.findByNaturalIdOrCreate(expectedFileCopy.getNaturalId(), () -> null);

        assertSame(result, expectedFileCopy);
    }

    @Test
    void findByNaturalIdOrCreateShouldFindGivenNotExists() {
        persistSampleDependencies();
        FileCopy expectedFileCopy = TestFileCopy.trackedBuilder()
                .id(new FileCopyId("7bf408f7-9b6e-4ee7-a2f2-27a454a4f5ba"))
                .naturalId(new FileCopyNaturalId(
                        SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                        SampleBackupTargets.LOCAL_FOLDER_1.get().getId()
                ))
                .build();
        FileCopy result = repository.findByNaturalIdOrCreate(expectedFileCopy.getNaturalId(), () -> expectedFileCopy);

        assertSame(result, expectedFileCopy);
    }

    @Test
    void findByNaturalIdOrCreateShouldFindGivenCreatedInParallel() {
        persistSampleData();
        FileCopySpringRepository mockSpringRepository = mock(FileCopySpringRepository.class);
        repository = new FileCopyJpaRepository(mockSpringRepository, entityMapper,
                mock(SpringPageMapper.class), mock(SpringPageableMapper.class), domainEventPublisher);
        FileCopy expectedExisting = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();
        FileCopyNaturalId naturalId = expectedExisting.getNaturalId();
        FileCopy newFileCopy = TestFileCopy.trackedBuilder()
                .naturalId(naturalId)
                .build();
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

        FileCopy result = repository.findByNaturalIdOrCreate(naturalId, () -> newFileCopy);

        assertSame(result, expectedExisting);
    }

    @Test
    void shouldFindOldestEnqueued() {
        persistSampleData();
        Optional<FileCopy> result = repository.findOldestEnqueued();

        FileCopy expectedResult = SampleFileCopies.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get();
        assertThat(result).isPresent();
        assertSame(result.get(), expectedResult);
    }

    @Test
    void shouldFindAllInProgressOrEnqueuedInOrderOfStatusThenDateModifiedAscending() {
        persistSampleData();
        var pagination = new Pagination(0, 3);

        Page<FileCopy> result = repository.findAllInProgressOrEnqueued(pagination);

        List<FileCopy> expectedItems = List.of(
                SampleFileCopies.IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get(),
                SampleFileCopies.ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2.get()
        );
        int totalPages = 1;
        int totalElements = 3;
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
                totalPages,
                totalElements,
                pagination
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
    void shouldFindAllInProgress() {
        persistSampleData();
        List<FileCopy> result = repository.findAllInProgress();

        List<FileCopy> expectedResult = List.of(
                SampleFileCopies.IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2.get()
        );
        assertSame(result, expectedResult);
    }

    private void assertSame(List<FileCopy> result, List<FileCopy> expectedResult) {
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
        assertThat(result).containsExactlyElementsOf(expectedResult);
    }

    @Test
    void shouldFindAllBySourceFileId() {
        persistSampleData();
        List<FileCopy> result =
                repository.findAllBySourceFileId(SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get().getId());

        List<FileCopy> expectedResult = List.of(
                SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get(),
                SampleFileCopies.TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get()
        );
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("dateCreated", "dateModified")
                .containsExactlyElementsOf(expectedResult);
    }

    @Test
    void existByBackupTargetIdAndStatusNotInShouldReturnTrueGivenFileCopyExists() {
        persistSampleData();
        BackupTargetId existingBackupTargetId =
                SampleFileCopies.TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get().getNaturalId()
                        .backupTargetId();

        boolean result = repository.existByBackupTargetIdAndStatusNotIn(
                existingBackupTargetId, List.of(FileCopyStatus.IN_PROGRESS));

        assertThat(result).isTrue();
    }

    @Test
    void existByBackupTargetIdAndStatusNotInShouldReturnFalseGivenFileCopyNotFoundWithCorrectStatusNot() {
        persistSampleData();
        BackupTargetId existingBackupTargetId =
                SampleFileCopies.TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1.get().getNaturalId()
                        .backupTargetId();

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
    void getUniqueBackupTargetIdsForStatusOtherThanShouldReturnIds() {
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

        assertThat(entityManager.find(FileCopyJpaEntity.class, fileCopy.getId().value())).isNull();
        assertThat(entityManager.find(FileCopyJpaEntity.class, unaffectedFileCopy.getId().value())).isNotNull();
    }

    @Test
    void deleteTrackedWithBackupTargetIdShouldNotDeleteFileCopiesWithoutMatchingStatus() {
        persistSampleData();
        FileCopy fileCopy = SampleFileCopies.TRACKED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_1.get();

        repository.deleteByBackupTargetIdAndStatusIn(
                fileCopy.getNaturalId().backupTargetId(), List.of(FileCopyStatus.FAILED));

        assertThat(entityManager.find(FileCopyJpaEntity.class, fileCopy.getId().value())).isNotNull();
    }

    private static class SampleGames {

        public static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle("Game 1")
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

        private static final BackupTargetJpaEntityMapper MAPPER = Mappers.getMapper(BackupTargetJpaEntityMapper.class);

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
                        .filePath("filePath1")
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> TRACKED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_1 =
                () -> TestFileCopy.trackedBuilder()
                        .id(new FileCopyId("773a79ae-6cfa-4264-b76e-7accffdb9f34"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_2.get().getId()
                        ))
                        .filePath("filePath2")
                        .dateModified(TODAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> IN_PROGRESS_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.inProgressBuilder()
                        .id(new FileCopyId("279baabb-f301-441b-a312-edf9babc84b2"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_3.get().getId()
                        ))
                        .filePath("filePath3")
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> ENQUEUED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("70fc9396-c7e1-4fe3-8718-7c79bac1cbb2"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_4.get().getId()
                        ))
                        .filePath("filePath4")
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> ENQUEUED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.enqueuedBuilder()
                        .id(new FileCopyId("4557eaf0-a603-4003-8a35-5ab588d24f88"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_5.get().getId()
                        ))
                        .filePath("filePath5")
                        .dateModified(TODAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> STORED_UNVERIFIED_FILE_COPY_FROM_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.storedIntegrityUnknownBuilder()
                        .id(new FileCopyId("e3e5636d-bb13-4506-87eb-c22d238defce"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_6.get().getId()
                        ))
                        .filePath("filePath6")
                        .dateModified(YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> STORED_VERIFIED_FILE_COPY_FROM_BEFORE_YESTERDAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.storedIntegrityVerifiedBuilder()
                        .id(new FileCopyId("3ff35991-86a4-4225-8d80-d157bd60193c"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_7.get().getId()
                        ))
                        .filePath("filePath7")
                        .dateModified(BEFORE_YESTERDAY.atStartOfDay())
                        .build();

        public static final Supplier<FileCopy> FAILED_FILE_COPY_FROM_TODAY_FOR_SOURCE_FILE_2 =
                () -> TestFileCopy.failedWithoutFilePathBuilder()
                        .id(new FileCopyId("5bac0242-14bc-435f-a187-387026cc8245"))
                        .naturalId(new FileCopyNaturalId(
                                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get().getId(),
                                SampleBackupTargets.LOCAL_FOLDER_8.get().getId()
                        ))
                        .filePath("filePath8")
                        .dateModified(TODAY.atStartOfDay())
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