package dev.codesoapbox.backity.core.backup.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileSourceIdTest {

    @Test
    void shouldCompareToEqualValues() {
        var id1 = new FileSourceId("test");
        var id2 = new FileSourceId("test");

        int result = id1.compareTo(id2);

        assertThat(result).isZero();
    }

    @Test
    void shouldCompareToDifferentValuesGivenFirstIsBeforeSecond() {
        var id1 = new FileSourceId("apple");
        var id2 = new FileSourceId("banana");

        int result = id1.compareTo(id2);
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void shouldCompareToDifferentValuesGivenFirstIsAfterSecond() {
        var id1 = new FileSourceId("banana");
        var id2 = new FileSourceId("apple");

        int result = id1.compareTo(id2);
        assertThat(result).isEqualTo(1);
    }
}