package dev.codesoapbox.backity.shared.adapters.driven.persistence;

import dev.codesoapbox.backity.shared.domain.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationEntityMapper {

    public Pageable toEntity(Pagination pagination) {
        return toEntity(pagination, Sort.unsorted());
    }

    public Pageable toEntity(Pagination pagination, Sort sort) {
        return PageRequest.of(pagination.pageNumber(), pagination.pageSize(), sort);
    }
}
