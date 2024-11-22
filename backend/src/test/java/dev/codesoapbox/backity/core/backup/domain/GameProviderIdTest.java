package dev.codesoapbox.backity.core.backup.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameProviderIdTest {

    @Test
    void shouldCompareToEqualValues() {
        var id1 = new GameProviderId("test");
        var id2 = new GameProviderId("test");

        int result = id1.compareTo(id2);

        assertThat(result).isZero();
    }

    @Test
    void shouldCompareToDifferentValuesGivenFirstIsBeforeSecond() {
        var id1 = new GameProviderId("apple");
        var id2 = new GameProviderId("banana");

        int result = id1.compareTo(id2);
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void shouldCompareToDifferentValuesGivenFirstIsAfterSecond() {
        var id1 = new GameProviderId("banana");
        var id2 = new GameProviderId("apple");

        int result = id1.compareTo(id2);
        assertThat(result).isEqualTo(1);
    }
}