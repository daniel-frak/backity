package dev.codesoapbox.backity.core.storagesolution.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StorageSolutionIdTest {

    @Test
    void shouldCreateFromString() {
        String expectedValue = "storageSolution1";
        var result = new StorageSolutionId(expectedValue);

        assertThat(result.value()).isEqualTo(expectedValue);
    }

    @Test
    void toStringShouldReturnValue() {
        String idString = "storageSolution1";
        var id = new StorageSolutionId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }

    @Nested
    class Comparable {

        @Test
        void shouldReturnZeroWhenComparingSameInstance() {
            var id1 = new StorageSolutionId("S3");

            assertThat(id1).isEqualByComparingTo(id1);
        }

        @Test
        void shouldMaintainComparableAsymmetry() {
            var id1 = new StorageSolutionId("S3");
            var id2 = new StorageSolutionId("LOCAL_FILE_SYSTEM");

            assertThat(id1.compareTo(id2)).isNotEqualTo(id2.compareTo(id1));
        }
    }
}