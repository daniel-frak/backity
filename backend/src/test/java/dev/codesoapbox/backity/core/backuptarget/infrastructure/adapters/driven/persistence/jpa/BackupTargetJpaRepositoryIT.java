package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetNotFoundException;
import dev.codesoapbox.backity.testing.jpa.DirectJpaPersistenceAdapter;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControl;
import dev.codesoapbox.backity.testing.time.FakeClock;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class BackupTargetJpaRepositoryIT {

    @Autowired
    protected BackupTargetJpaRepository repository;

    @Autowired
    protected FakeClock clock;

    @Autowired
    private DirectJpaPersistenceAdapter directPersistenceAdapter;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
    }

    void persistSampleData() {
        directPersistenceAdapter.persist(SampleBackupTargets.getAll());
    }

    private static final class Time {

        public static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;
        public static final LocalDateTime YESTERDAY = NOW.minusDays(1);
    }

    private static final class SampleBackupTargets {

        static final Supplier<BackupTarget> TODAY_LOCAL_FOLDER = () -> TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("68bc2d0b-1558-44a9-babd-c6d42048a993"))
                .withDateCreated(Time.NOW)
                .build();

        static final Supplier<BackupTarget> YESTERDAY_S3_BUCKET = () -> TestBackupTarget.s3BucketBuilder()
                .withId(new BackupTargetId("d94eaa11-0259-4aaa-9eaf-4c50acc42a82"))
                .withDateCreated(Time.YESTERDAY)
                .withDateModified(Time.YESTERDAY)
                .build();

        static List<BackupTarget> getAll() {
            return List.of(TODAY_LOCAL_FOLDER.get(), YESTERDAY_S3_BUCKET.get());
        }
    }

    @Nested
    class Save {
        @Test
        void shouldPersistNew() {
            BackupTarget backupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();

            repository.save(backupTarget);

            BackupTarget persistedAggregate = directPersistenceAdapter.getPersistedDomainObject(backupTarget);
            assertThat(persistedAggregate)
                    .usingRecursiveComparison()
                    .isEqualTo(backupTarget);
        }

        @Test
        void shouldModifyExisting() {
            persistSampleData();
            BackupTarget backupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();
            backupTarget.setName(new BackupTargetName("Changed name"));

            repository.save(backupTarget);

            BackupTarget persistedAggregate = directPersistenceAdapter.getPersistedDomainObject(backupTarget);
            assertThat(persistedAggregate)
                    .usingRecursiveComparison()
                    .isEqualTo(backupTarget);
        }

        @SuppressWarnings("JUnitMalformedDeclaration")
        @Test
        void shouldUpdateDatesGivenNew(EntityAuditControl entityAuditControl) {
            entityAuditControl.enable();
            BackupTarget backupTarget = SampleBackupTargets.YESTERDAY_S3_BUCKET.get();

            repository.save(backupTarget);

            LocalDateTime now = LocalDateTime.now(clock);
            BackupTarget persistedAggregate = directPersistenceAdapter.getPersistedDomainObject(backupTarget);
            assertThat(persistedAggregate.getDateCreated())
                    .isNotEqualTo(backupTarget.getDateCreated())
                    .isEqualTo(now);
            assertThat(persistedAggregate.getDateModified())
                    .isNotEqualTo(backupTarget.getDateModified())
                    .isEqualTo(now);
        }

        @SuppressWarnings("JUnitMalformedDeclaration")
        @Test
        void shouldUpdateDatesGivenExisting(EntityAuditControl entityAuditControl) {
            persistSampleData();
            BackupTarget backupTarget = SampleBackupTargets.YESTERDAY_S3_BUCKET.get();
            backupTarget.setName(new BackupTargetName("Changed name"));
            entityAuditControl.enable();

            repository.save(backupTarget);

            LocalDateTime now = LocalDateTime.now(clock);
            BackupTarget persistedAggregate = directPersistenceAdapter.getPersistedDomainObject(backupTarget);
            assertThat(persistedAggregate.getDateCreated())
                    .isEqualTo(backupTarget.getDateCreated())
                    .isNotEqualTo(now);
            assertThat(persistedAggregate.getDateModified())
                    .isNotEqualTo(backupTarget.getDateModified())
                    .isEqualTo(now);
        }
    }

    @Nested
    class GetById {

        @Test
        void shouldReturnAggregateGivenItExists() {
            persistSampleData();
            BackupTarget expectedResult = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();

            BackupTarget result = repository.getById(expectedResult.getId());

            assertThat(result)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void shouldThrowGivenNotFound() {
            var nonExistentId = new BackupTargetId("247900d6-8829-47cd-a068-4f35f37d8eb8");

            assertThatThrownBy(() -> repository.getById(nonExistentId))
                    .isInstanceOf(BackupTargetNotFoundException.class)
                    .hasMessageContaining(nonExistentId.toString());
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldReturnEmptyListGivenNothingFound() {
            List<BackupTarget> result = repository.findAll();

            assertThat(result).isEmpty();
        }

        @Test
        void shouldReturnAllDataForAggregate() {
            BackupTarget backupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();
            directPersistenceAdapter.persist(backupTarget);

            List<BackupTarget> result = repository.findAll();

            assertThat(result)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(backupTarget);
        }

        @Test
        void shouldSortByDateCreatedAsc() {
            persistSampleData();

            List<BackupTarget> result = repository.findAll();

            assertThat(result)
                    .containsExactly(
                            SampleBackupTargets.YESTERDAY_S3_BUCKET.get(),
                            SampleBackupTargets.TODAY_LOCAL_FOLDER.get()
                    );
        }
    }

    @Nested
    class FindAllByIdIn {

        @Test
        void shouldReturnEmptyListGivenNothingFound() {
            var nonExistentId = new BackupTargetId("247900d6-8829-47cd-a068-4f35f37d8eb8");

            List<BackupTarget> result = repository.findAllByIdIn(List.of(nonExistentId));

            assertThat(result).isEmpty();
        }

        @Test
        void shouldReturnAllDataForAggregate() {
            persistSampleData();
            BackupTarget expectedBackupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();

            List<BackupTarget> result = repository.findAllByIdIn(List.of(expectedBackupTarget.getId()));

            assertThat(result)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(expectedBackupTarget);
        }

        @Test
        void shouldSortByDateCreatedAsc() {
            persistSampleData();
            BackupTarget todayBackupTarget = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();
            BackupTarget yesterdayBackupTarget = SampleBackupTargets.YESTERDAY_S3_BUCKET.get();

            List<BackupTarget> result = repository.findAllByIdIn(
                    List.of(todayBackupTarget.getId(), yesterdayBackupTarget.getId()));

            assertThat(result)
                    .containsExactly(
                            SampleBackupTargets.YESTERDAY_S3_BUCKET.get(),
                            SampleBackupTargets.TODAY_LOCAL_FOLDER.get()
                    );
        }
    }

    @Nested
    class DeleteById {

        @Test
        void shouldDeleteAggregate() {
            persistSampleData();
            BackupTarget aggregateToDelete = SampleBackupTargets.TODAY_LOCAL_FOLDER.get();

            repository.deleteById(aggregateToDelete.getId());

            assertThat(directPersistenceAdapter.exists(aggregateToDelete)).isFalse();
        }
    }
}
