package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence;

import dev.codesoapbox.backity.shared.domain.Page;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageEntityMapperTest {

    private static final PageEntityMapper MAPPER = new PageEntityMapper();

    @Test
    void shouldMapToDomain() {
        org.springframework.data.domain.Page<String> entityPage = new PageImpl<>(List.of("String1", "String2"),
                PageRequest.of(5, 3), 6);

        Page<String> result = MAPPER.toDomain(entityPage, c -> c + "_mapped");

        Page<String> expectedResult = new Page<>(List.of("String1_mapped", "String2_mapped"), 3, 6,
                17, 3, 5);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}