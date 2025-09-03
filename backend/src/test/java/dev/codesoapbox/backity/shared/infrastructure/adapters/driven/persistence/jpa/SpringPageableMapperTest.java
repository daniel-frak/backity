package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class SpringPageableMapperTest {

    private static final SpringPageableMapper MAPPER = new SpringPageableMapper();

    @Test
    void shouldMapToPageable() {
        var pagination = new Pagination(1, 2);

        Pageable result = MAPPER.toPageable(pagination);

        Pageable expectedResult = PageRequest.of(1, 2);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldMapToPageableWithSort() {
        var pagination = new Pagination(1, 2);
        var sort = Sort.by(Sort.Direction.ASC, "dateCreated");

        Pageable result = MAPPER.toPageable(pagination, sort);

        Pageable expectedResult = PageRequest.of(1, 2, sort);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}