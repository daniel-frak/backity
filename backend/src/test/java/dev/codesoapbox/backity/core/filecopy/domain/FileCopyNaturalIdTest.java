package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileCopyNaturalIdTest {

    @Nested
    class Creation {
        @Test
        void shouldCreate() {
            var gameFileId = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
            var backupTargetId = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");

            var result = new FileCopyNaturalId(gameFileId, backupTargetId);

            assertThat(result).isNotNull();
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullGameFileId() {
            var backupTargetId = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");

            assertThatThrownBy(() -> new FileCopyNaturalId(null, backupTargetId))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("gameFileId");
        }

        @SuppressWarnings("DataFlowIssue")
        @Test
        void constructorShouldThrowGivenNullBackupTargetId() {
            var gameFileId = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

            assertThatThrownBy(() -> new FileCopyNaturalId(gameFileId, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("backupTargetId");
        }
    }

    @Nested
    class Comparable {

        @Test
        void shouldReturnZeroWhenComparingSameInstance() {
            var id1 = new FileCopyNaturalId(
                    new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"),
                    new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76")
            );

            assertThat(id1).isEqualByComparingTo(id1);
        }

        @Test
        void shouldCompareByGameFileId() {
            var id1 = new FileCopyNaturalId(
                    new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"),
                    new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76")
            );
            var id2 = new FileCopyNaturalId(
                    new GameFileId("a6adc122-df20-4e2c-a975-7d4af7104704"),
                    new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76")
            );

            assertThat(id1.compareTo(id2)).isNotEqualTo(id2.compareTo(id1));
        }

        @Test
        void shouldCompareByBackupTargetId() {
            var id1 = new FileCopyNaturalId(
                    new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"),
                    new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76")
            );
            var id2 = new FileCopyNaturalId(
                    new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"),
                    new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7")
            );

            assertThat(id1.compareTo(id2)).isNotEqualTo(id2.compareTo(id1));
        }
    }
}