package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.domain.Page;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageHttpDtoMapperTest {

    private static final PageHttpDtoMapper MAPPER = Mappers.getMapper(PageHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        Page<Integer> page = new Page<>(List.of(99), 4, 3,
                2, 1, 0);

        PageHttpDto<String> result = MAPPER.toDto(page, c -> c + "_mapped");

        PageHttpDto<String> expectedResult = new PageHttpDto<>(List.of("99_mapped"),
                4, 3, 2, 1, 0);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}