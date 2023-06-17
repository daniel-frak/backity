package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.shared.domain.Page;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageJsonMapperTest {

    private static final PageJsonMapper MAPPER = Mappers.getMapper(PageJsonMapper.class);

    @Test
    void shouldMapToJson() {
        Page<Integer> page = new Page<>(List.of(99), 4, 3,
                2, 1, 0);

        PageJson<String> result = MAPPER.toJson(page, c -> c + "_mapped");

        PageJson<String> expectedResult = new PageJson<>(List.of("99_mapped"),
                4, 3, 2, 1, 0);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}