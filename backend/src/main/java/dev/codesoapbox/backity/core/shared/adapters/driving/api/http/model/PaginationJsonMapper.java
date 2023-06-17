package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class PaginationJsonMapper {

    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "pageNumber", source = "page", defaultValue = "0")
    public abstract Pagination toModel(PaginationJson pagination);
}
