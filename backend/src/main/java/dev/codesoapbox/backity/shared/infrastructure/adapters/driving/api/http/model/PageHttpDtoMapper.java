package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.function.Function;

@Mapper
public abstract class PageHttpDtoMapper {

    // These casts are safe because the public method signature guarantees type safety.
    @SuppressWarnings("unchecked")
    public <T, U> PageHttpDto<U> toDto(Page<T> page, Function<T, U> contentMapper) {
        Page<U> mappedPage = page.map(contentMapper);
        return (PageHttpDto<U>) toDtoInternal((Page<Object>) mappedPage);
    }

    protected abstract PageHttpDto<Object> toDtoInternal(Page<Object> page);

    @Mapping(target = "size", source = "pageSize")
    @Mapping(target = "page", source = "pageNumber")
    protected abstract ResponsePaginationHttpDto toPaginationDto(Pagination pagination);
}
