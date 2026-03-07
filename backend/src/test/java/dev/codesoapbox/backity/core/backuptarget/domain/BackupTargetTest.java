package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BackupTargetTest {

    @Nested
    class Create {

        @Test
        void shouldReturnBackupTarget() {
            String expectedName = "Local folder";
            StorageSolutionId expectedStorageSolution = new StorageSolutionId("storageSolution1");
            String expectedPathTemplate = "games/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}";

            BackupTarget result = BackupTarget.create(
                    expectedName,
                    expectedStorageSolution,
                    expectedPathTemplate
            );

            assertThat(result.getName()).isEqualTo(expectedName);
            assertThat(result.getStorageSolutionId()).isEqualTo(expectedStorageSolution);
            assertThat(result.getPathTemplate()).isEqualTo(expectedPathTemplate);
        }
    }
}