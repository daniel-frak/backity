package dev.codesoapbox.backity.shared.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageTest {

    @Test
    void shouldMap() {
        var pagination = new Pagination(0, 1);
        Page<Integer> page = new Page<>(List.of(99), 3, 2, pagination);

        Page<String> result = page.map(c -> c + "_mapped");

        Page<String> expectedPage = new Page<>(List.of("99_mapped"), 3, 2, pagination);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedPage);
    }

    @Test
    void asEmptyShouldReturnEmptyPage() {
        var pagination = new Pagination(0, 1);
        Page<Integer> page = new Page<>(List.of(123), 3, 2, pagination);

        Page<String> result = page.asEmpty();

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(page);
        assertThat(result.content()).isEmpty();
    }
}