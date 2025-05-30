package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.shared.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PageEntityMapper {

    public <T, U> Page<T> toDomain(org.springframework.data.domain.Page<U> entityPage, Function<U, T> contentMapper) {
        List<T> mappedContent = entityPage.map(contentMapper)
                .getContent();
        return new Page<>(mappedContent, entityPage.getSize(), entityPage.getTotalPages(),
                entityPage.getTotalElements(), entityPage.getPageable().getPageSize(),
                entityPage.getPageable().getPageNumber());
    }
}
