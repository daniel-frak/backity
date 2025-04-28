package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.domain.Pagination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PaginationHttpDtoMapper {

    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "pageNumber", source = "page", defaultValue = "0")
    Pagination toModel(PaginationHttpDto pagination);
}
