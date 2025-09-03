package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

public class SpringPageMapper {

    public <T, U> Page<T> toDomain(org.springframework.data.domain.Page<U> entityPage, Function<U, T> contentMapper) {
        List<T> mappedContent = entityPage.map(contentMapper)
                .getContent();

        Pagination pagination = null;
        Pageable pageable = entityPage.getPageable();
        if(pageable.isPaged()) {
            pagination = new Pagination(pageable.getPageNumber(), pageable.getPageSize());
        }
        int totalPages = entityPage.getTotalPages();
        long totalElements = entityPage.getTotalElements();
        return new Page<>(mappedContent, totalPages, totalElements, pagination);
    }
}
