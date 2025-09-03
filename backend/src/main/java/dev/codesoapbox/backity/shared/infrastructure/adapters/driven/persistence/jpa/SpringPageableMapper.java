package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.shared.domain.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SpringPageableMapper {

    public Pageable toPageable(Pagination pagination) {
        return toPageable(pagination, Sort.unsorted());
    }

    public Pageable toPageable(Pagination pagination, Sort sort) {
        return PageRequest.of(pagination.pageNumber(), pagination.pageSize(), sort);
    }
}
