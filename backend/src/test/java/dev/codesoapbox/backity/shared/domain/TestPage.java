package dev.codesoapbox.backity.shared.domain;

import java.util.List;

public class TestPage {

    public static <T> Page<T> of(List<T> content, Pagination pagination) {
        return new Page<>(content, content.size(), 1, content.size(),
                pagination.pageSize(), pagination.pageNumber());
    }
}