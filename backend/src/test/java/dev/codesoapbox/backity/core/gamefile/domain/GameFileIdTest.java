package dev.codesoapbox.backity.core.gamefile.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameFileIdTest {

    @Test
    void toStringShouldReturnValue() {
        String idString = "3b21cc23-54c6-48f3-914d-188b790128b4";
        var id = new GameFileId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }

    @Nested
    class Creation {
        @Test
        void shouldCreateFromString() {
            var result = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

            var expectedValue = UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48");
            assertThat(result.value()).isEqualTo(expectedValue);
        }

        @Test
        void shouldCreateNewInstance() {
            GameFileId result = GameFileId.newInstance();

            assertThat(result.value()).isNotNull();
        }
    }

    @Nested
    class Comparable {

        @Test
        void shouldReturnZeroWhenComparingSameInstance() {
            var id1 = new GameFileId("3b21cc23-54c6-48f3-914d-188b790128b4");

            assertThat(id1).isEqualByComparingTo(id1);
        }

        @Test
        void shouldMaintainComparableAsymmetry() {
            var id1 = new GameFileId("3b21cc23-54c6-48f3-914d-188b790128b4");
            var id2 = new GameFileId("a6adc122-df20-4e2c-a975-7d4af7104704");

            assertThat(id1.compareTo(id2)).isNotEqualTo(id2.compareTo(id1));
        }
    }
}