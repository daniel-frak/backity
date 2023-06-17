package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.shared.domain.Page;
import org.mapstruct.Mapper;

import java.util.function.Function;

@Mapper
public abstract class PageJsonMapper {

    // These casts are safe because the public method signature guarantees type safety.
    @SuppressWarnings("unchecked")
    public <T, U> PageJson<U> toJson(Page<T> page, Function<T, U> contentMapper) {
        Page<U> mappedPage = page.map(contentMapper);
        return (PageJson<U>) toJsonInternal((Page<Object>) mappedPage);
    }

    protected abstract PageJson<Object> toJsonInternal(Page<Object> page);
}
