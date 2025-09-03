package dev.codesoapbox.backity.shared.domain;

import lombok.NonNull;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public record Page<T>(
        @NonNull List<T> content,
        int totalPages,
        long totalElements,
        Pagination pagination // Might be null if unpaged
) {
    public <U> Page<U> map(Function<T, U> contentMapper) {
        List<U> mappedContent = content.stream()
                .map(contentMapper)
                .toList();
        return new Page<>(mappedContent, totalPages, totalElements, pagination);
    }

    public <U> Page<U> asEmpty() {
        List<U> noContent = emptyList();
        return new Page<>(noContent, totalPages, totalElements, pagination);
    }
}
