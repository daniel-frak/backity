package dev.codesoapbox.backity.core.backuptarget.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BackupTargetIdTest {

    @Test
    void toStringShouldReturnValue() {
        String idString = "3553a3c7-47a7-4f7a-8b47-75928bee37d0";
        var id = new BackupTargetId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }

    @Nested
    class Creation {
        @Test
        void shouldCreateFromString() {
            var result = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");

            var expectedValue = UUID.fromString("3553a3c7-47a7-4f7a-8b47-75928bee37d0");
            assertThat(result.value()).isEqualTo(expectedValue);
        }

        @Test
        void shouldCreateNewInstance() {
            BackupTargetId result = BackupTargetId.newInstance();

            assertThat(result.value()).isNotNull();
        }
    }

    @Nested
    class Comparable {

        @Test
        void shouldReturnZeroWhenComparingSameInstance() {
            var id1 = new BackupTargetId("d46dde81-e519-4300-9a54-6f9e7d637926");

            assertThat(id1).isEqualByComparingTo(id1);
        }

        @Test
        void shouldMaintainComparableAsymmetry() {
            var id1 = new BackupTargetId("d46dde81-e519-4300-9a54-6f9e7d637926");
            var id2 = new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7");

            assertThat(id1.compareTo(id2)).isNotEqualTo(id2.compareTo(id1));
        }
    }
}