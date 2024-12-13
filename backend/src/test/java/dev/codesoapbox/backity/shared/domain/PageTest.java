package dev.codesoapbox.backity.shared.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageTest {

    @Test
    void shouldMap() {
        Page<Integer> page = new Page<>(List.of(99), 4, 3, 2, 1, 0);

        Page<String> result = page.map(c -> c + "_mapped");

        Page<String> expectedPage = new Page<>(List.of("99_mapped"),
                4, 3, 2, 1, 0);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedPage);
    }
}