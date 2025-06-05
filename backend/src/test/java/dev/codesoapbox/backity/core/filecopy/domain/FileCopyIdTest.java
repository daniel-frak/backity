package dev.codesoapbox.backity.core.filecopy.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyIdTest {

    @Test
    void toStringShouldReturnValue() {
        String idString = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var id = new FileCopyId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }

    @Nested
    class Creation {
        @Test
        void shouldCreateFromString() {
            var result = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");

            var expectedValue = UUID.fromString("6df888e8-90b9-4df5-a237-0cba422c0310");
            assertThat(result.value()).isEqualTo(expectedValue);
        }

        @Test
        void shouldCreateNewInstance() {
            FileCopyId result = FileCopyId.newInstance();

            assertThat(result.value()).isNotNull();
        }
    }

    @Nested
    class Comparable {

        @Test
        void shouldReturnZeroWhenComparingSameInstance() {
            var id1 = new FileCopyId("9fdad52f-b4a6-46bc-af6d-bf27f9661eae");

            assertThat(id1).isEqualByComparingTo(id1);
        }

        @Test
        void shouldMaintainComparableAsymmetry() {
            var id1 = new FileCopyId("9fdad52f-b4a6-46bc-af6d-bf27f9661eae");
            var id2 = new FileCopyId("773a79ae-6cfa-4264-b76e-7accffdb9f34");

            assertThat(id1.compareTo(id2)).isNotEqualTo(id2.compareTo(id1));
        }
    }
}