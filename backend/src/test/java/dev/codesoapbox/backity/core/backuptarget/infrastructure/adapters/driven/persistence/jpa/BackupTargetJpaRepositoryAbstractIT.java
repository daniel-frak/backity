package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetNotFoundException;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MultiDatabaseRepositoryTest
abstract class BackupTargetJpaRepositoryAbstractIT {

    private final Persist persist = new Persist();
    private final Assertions assertThat = new Assertions();

    @Autowired
    protected BackupTargetJpaRepository repository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected BackupTargetJpaEntityMapper entityMapper;

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
        backupTarget.setName("Changed name");

        repository.save(backupTarget);
        entityManager.flush();

        assertThat.wasPersistedWithAllData(backupTarget);
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

        private static final String[] ENTITY_FIELDS_TO_IGNORE = {"dateCreated", "dateModified"};

        void wasPersistedWithAllData(BackupTarget expected) {
            BackupTargetJpaEntity persistedEntity =
                    entityManager.find(BackupTargetJpaEntity.class, expected.getId().value());
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
                    .ignoringFields(ENTITY_FIELDS_TO_IGNORE)
                    .isEqualTo(expected);
        }

        void containsOnlyWithAllData(List<BackupTarget> actual, BackupTarget expected) {
            assertThat(actual)
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields(ENTITY_FIELDS_TO_IGNORE)
                    .containsExactly(expected);
        }

        void containsInOrder(List<BackupTarget> actual, BackupTarget... expected) {
            assertThat(actual)
                    .containsExactly(expected);
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