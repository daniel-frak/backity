package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageHttpDtoMapperTest {

    private static final PageHttpDtoMapper MAPPER = Mappers.getMapper(PageHttpDtoMapper.class);

    @Test
    void shouldMapToDto() {
        Page<Integer> page = new Page<>(List.of(99), 2,
                2, new Pagination(0, 1));

        PageHttpDto<String> result = MAPPER.toDto(page, c -> c + "_mapped");

        PageHttpDto<String> expectedResult = new PageHttpDto<>(List.of("99_mapped"),
                2, 2, new PaginationHttpDto(0, 1));
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}