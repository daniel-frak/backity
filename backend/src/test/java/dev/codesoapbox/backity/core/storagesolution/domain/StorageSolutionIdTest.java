package dev.codesoapbox.backity.core.storagesolution.domain;

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

}