package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RequestPaginationHttpDtoMapperTest {

    private static final RequestPaginationHttpDtoMapper MAPPER =
            Mappers.getMapper(RequestPaginationHttpDtoMapper.class);

    @Test
    void shouldMapToDomainGivenPageIsNotNull() {
        var dto = new RequestPaginationHttpDto(0, 1);

        Pagination result = MAPPER.toModel(dto);

        Pagination expectedResult = new Pagination(0, 1);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldMapToDomainGivenValuesAreNull() {
        var dto = new RequestPaginationHttpDto(null, null);

        Pagination result = MAPPER.toModel(dto);

        Pagination expectedResult = new Pagination(0, 20);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}