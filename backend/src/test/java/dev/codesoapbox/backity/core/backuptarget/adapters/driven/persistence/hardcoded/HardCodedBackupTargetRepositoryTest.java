package dev.codesoapbox.backity.core.backuptarget.adapters.driven.persistence.hardcoded;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.BackupTargetNotFoundException;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem.LocalFileSystemStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem.S3StorageSolution;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HardCodedBackupTargetRepositoryTest {

    @Nested
    class Creation {

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenPathTemplateIsNull() {
            assertThatThrownBy(() -> new HardCodedBackupTargetRepository(
                    true, true, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("pathTemplate");
        }
    }

    @Nested
    class Retrieval {

        @Test
        void findAllShouldReturnS3GivenItsEnabled() {
            var pathTemplate = "somePathTemplate";
            var repository = new HardCodedBackupTargetRepository(true, false, pathTemplate);

            List<BackupTarget> result = repository.findAll();

            List<BackupTarget> expectedResult = List.of(new BackupTarget(
                    new BackupTargetId("d46dde81-e519-4300-9a54-6f9e7d637926"),
                    S3StorageSolution.ID,
                    "S3 bucket",
                    pathTemplate
            ));
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void findAllShouldReturnLocalFolderGivenItsEnabled() {
            var pathTemplate = "somePathTemplate";
            var repository = new HardCodedBackupTargetRepository(false, true, pathTemplate);

            List<BackupTarget> result = repository.findAll();

            List<BackupTarget> expectedResult = List.of(new BackupTarget(
                    new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7"),
                    LocalFileSystemStorageSolution.ID,
                    "Local folder",
                    pathTemplate
            ));
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void shouldFindById() {
            var pathTemplate = "somePathTemplate";
            var repository = new HardCodedBackupTargetRepository(true, true, pathTemplate);

            BackupTarget result = repository.getById(new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7"));

            BackupTarget expectedResult = new BackupTarget(
                    new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7"),
                    LocalFileSystemStorageSolution.ID,
                    "Local folder",
                    pathTemplate
            );
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void findByIdShouldThrowGivenNotFound() {
            var pathTemplate = "somePathTemplate";
            var repository = new HardCodedBackupTargetRepository(false, false, pathTemplate);
            var nonexistentId = new BackupTargetId("dfa75f83-9907-4619-8e74-72d58326b3fb");

            assertThatThrownBy(() -> repository.getById(nonexistentId))
                    .isInstanceOf(BackupTargetNotFoundException.class)
                    .hasMessageContaining(nonexistentId.toString());
        }

        @Test
        void findAllByIdInShouldReturnFoundBackupTargets() {
            var pathTemplate = "somePathTemplate";
            var repository = new HardCodedBackupTargetRepository(true, true, pathTemplate);

            List<BackupTarget> result = repository.findAllByIdIn(
                    List.of(new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7")));

            List<BackupTarget> expectedResult = List.of(new BackupTarget(
                    new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7"),
                    LocalFileSystemStorageSolution.ID,
                    "Local folder",
                    pathTemplate
            ));
            assertThat(result).usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }
    }
}