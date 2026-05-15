package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetNotFoundException;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControl;
import dev.codesoapbox.backity.testing.time.FakeClock;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class BackupTargetJpaRepositoryIT {

    private final Persist persist = new Persist();
    private final Assertions assertThat = new Assertions();

    @Autowired
    protected BackupTargetJpaRepository repository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected BackupTargetJpaEntityMapper entityMapper;

    @Autowired
    protected FakeClock clock;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
    }

    @Test
    void saveShouldPersistNew() {
        BackupTarget backupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();

        repository.save(backupTarget);
        entityManager.flush();

        assertThat.wasPersistedWithAllData(backupTarget);
    }

    @Test
    void saveShouldModifyExisting() {
        BackupTarget backupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();
        persist.backupTarget(backupTarget);
        backupTarget.setName(new BackupTargetName("Changed name"));

        repository.save(backupTarget);
        entityManager.flush();

        assertThat.wasPersistedWithAllData(backupTarget);
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDates(EntityAuditControl entityAuditControl) {
        entityAuditControl.enable();
        BackupTarget backupTarget = SampleBackupTargets.YESTERDAY_LOCAL_FOLDER.get();

        repository.save(backupTarget);
        entityManager.flush();

        assertThat.datesWereUpdatedByAuditingHandler(backupTarget);
    }

    @Test
    void getByIdShouldReturnAggregateGivenItExists() {
        persist.backupTargets(SampleBackupTargets.getAll());
        BackupTarget expectedBackupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();

        BackupTarget result = repository.getById(expectedBackupTarget.getId());

        assertThat.allDataIsSame(result, expectedBackupTarget);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        var nonExistentId = new BackupTargetId("247900d6-8829-47cd-a068-4f35f37d8eb8");

        assertThatThrownBy(() -> repository.getById(nonExistentId))
                .isInstanceOf(BackupTargetNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    @Test
    void findAllShouldReturnEmptyListGivenNothingFound() {
        List<BackupTarget> result = repository.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findAllShouldReturnAllDataForAggregate() {
        BackupTarget backupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();
        persist.backupTarget(backupTarget);

        List<BackupTarget> result = repository.findAll();

        assertThat.containsOnlyWithAllData(result, backupTarget);
    }

    @Test
    void findAllShouldSortByDateCreatedAsc() {
        persist.backupTargets(SampleBackupTargets.getAll());

        List<BackupTarget> result = repository.findAll();

        assertThat.containsInOrder(result,
                SampleBackupTargets.YESTERDAY_LOCAL_FOLDER.get(),
                SampleBackupTargets.TODAY_LOCAL_FOLDER.get()
        );
    }

    @Test
    void findAllByIdInShouldReturnEmptyListGivenNothingFound() {
        var nonExistentId = new BackupTargetId("247900d6-8829-47cd-a068-4f35f37d8eb8");

        List<BackupTarget> result = repository.findAllByIdIn(List.of(nonExistentId));

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByIdInShouldReturnAllDataForAggregate() {
        persist.backupTargets(SampleBackupTargets.getAll());
        BackupTarget expectedBackupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();

        List<BackupTarget> result = repository.findAllByIdIn(List.of(expectedBackupTarget.getId()));

        assertThat.containsOnlyWithAllData(result, expectedBackupTarget);
    }

    @Test
    void findAllByIdInShouldSortByDateCreatedAsc() {
        persist.backupTargets(SampleBackupTargets.getAll());
        BackupTarget todayBackupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();
        BackupTarget yesterdayBackupTarget = SampleBackupTargets.YESTERDAY_LOCAL_FOLDER.get();

        List<BackupTarget> result = repository.findAllByIdIn(
                List.of(todayBackupTarget.getId(), yesterdayBackupTarget.getId()));

        assertThat.containsInOrder(result,
                SampleBackupTargets.YESTERDAY_LOCAL_FOLDER.get(),
                SampleBackupTargets.TODAY_LOCAL_FOLDER.get()
        );
    }

    @Test
    void deleteByIdShouldDeleteAggregate() {
        BackupTarget aggregateToDelete = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();
        persist.backupTargets(aggregateToDelete);

        repository.deleteById(aggregateToDelete.getId());

        assertThat.doesNotExist(aggregateToDelete);
    }

    private static class Time {

        private static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;
        private static final LocalDateTime YESTERDAY = NOW.minusDays(1);
    }

    private static class SampleBackupTargets {

        static final Supplier<BackupTarget> TODAY_LOCAL_FOLDER = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("68bc2d0b-1558-44a9-babd-c6d42048a993"))
                .withDateCreated(Time.NOW)
                .build();

        static final Supplier<BackupTarget> YESTERDAY_LOCAL_FOLDER = () -> TestBackupTarget.s3BucketBuilder()
                .withId(new BackupTargetId("d94eaa11-0259-4aaa-9eaf-4c50acc42a82"))
                .withDateCreated(Time.YESTERDAY)
                .build();

        static List<BackupTarget> getAll() {
            return List.of(TODAY_LOCAL_FOLDER.get(), YESTERDAY_LOCAL_FOLDER.get());
        }
    }

    private class Assertions {

        void datesWereUpdatedByAuditingHandler(BackupTarget backupTarget) {
            LocalDateTime now = LocalDateTime.now(clock);
            BackupTargetJpaEntity persistedEntity = getPersistedEntity(backupTarget.getId());
            assertThat(persistedEntity.getDateCreated())
                    .isNotEqualTo(backupTarget.getDateCreated())
                    .isEqualTo(now);
            assertThat(persistedEntity.getDateModified())
                    .isNotEqualTo(backupTarget.getDateModified())
                    .isEqualTo(now);
        }

        private BackupTargetJpaEntity getPersistedEntity(BackupTargetId id) {
            return entityManager.find(BackupTargetJpaEntity.class, id.value());
        }

        void wasPersistedWithAllData(BackupTarget expected) {
            BackupTargetJpaEntity persistedEntity = getPersistedEntity(expected.getId());
            BackupTarget persisted = entityMapper.toDomain(persistedEntity);
            allDataIsSame(persisted, expected);
        }

        void allDataIsSame(BackupTarget actual, BackupTarget expected) {
            assertThat(actual)
                    .satisfies(it -> {
                        assertThat(it).isNotNull();
                        assertThat(it.getDateCreated()).isNotNull();
                        assertThat(it.getDateModified()).isNotNull();
                    })
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        void containsOnlyWithAllData(List<BackupTarget> actual, BackupTarget expected) {
            assertThat(actual)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(expected);
        }

        void containsInOrder(List<BackupTarget> actual, BackupTarget... expected) {
            assertThat(actual)
                    .containsExactly(expected);
        }

        public void doesNotExist(BackupTarget aggregate) {
            BackupTargetJpaEntity entity = entityManager.find(BackupTargetJpaEntity.class, aggregate.getId().value());
            assertThat(entity).isNull();
        }
    }

    private class Persist {

        void backupTarget(BackupTarget backupTarget) {
            backupTargets(backupTarget);
        }

        void backupTargets(BackupTarget... backupTargets) {
            backupTargets(List.of(backupTargets));
        }

        void backupTargets(List<BackupTarget> backupTargets) {
            for (BackupTarget backupTarget : backupTargets) {
                entityManager.persist(entityMapper.toEntity(backupTarget));
            }
            entityManager.flush();
        }
    }
}