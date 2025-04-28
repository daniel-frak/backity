package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence;

import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationEntityMapperTest {

    private static final PaginationEntityMapper MAPPER = new PaginationEntityMapper();

    @Test
    void shouldMapToEntity() {
        var pagination = new Pagination(1, 2);

        Pageable result = MAPPER.toEntity(pagination);

        Pageable expectedResult = PageRequest.of(1, 2);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldMapToEntityWithSort() {
        var pagination = new Pagination(1, 2);
        var sort = Sort.by(Sort.Direction.ASC, "dateCreated");

        Pageable result = MAPPER.toEntity(pagination, sort);

        Pageable expectedResult = PageRequest.of(1, 2, sort);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}