package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.domain.Pagination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RequestPaginationHttpDtoMapper {

    @Mapping(target = "pageSize", source = "size", defaultValue = "20")
    @Mapping(target = "pageNumber", source = "page")
    Pagination toModel(RequestPaginationHttpDto pagination);
}
