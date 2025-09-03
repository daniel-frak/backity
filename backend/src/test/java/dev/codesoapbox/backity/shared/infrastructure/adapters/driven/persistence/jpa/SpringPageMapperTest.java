package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpringPageMapperTest {

    private static final SpringPageMapper MAPPER = new SpringPageMapper();

    @Test
    void shouldMapToDomain() {
        org.springframework.data.domain.Page<String> springPage = new PageImpl<>(
                List.of("String1", "String2"),
                PageRequest.of(5, 3),
                17
        );

        Page<String> result = MAPPER.toDomain(springPage, c -> c + "_mapped");

        Page<String> expectedResult = new Page<>(
                List.of("String1_mapped", "String2_mapped"),
                6,
                17,
                new Pagination(5, 3)
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldMapUnpagedToDomain() {
        org.springframework.data.domain.Page<String> entityPage = new PageImpl<>(
                List.of("String1", "String2"),
                Pageable.unpaged(),
                2
        );

        Page<String> result = MAPPER.toDomain(entityPage, c -> c + "_mapped");

        Page<String> expectedResult = new Page<>(
                List.of("String1_mapped", "String2_mapped"),
                1,
                2,
                null
        );
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}